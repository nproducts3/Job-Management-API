import os
from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional
import spacy
from dotenv import load_dotenv
import google.generativeai as genai
from google.generativeai.client import configure
from google.generativeai.generative_models import GenerativeModel

load_dotenv()
print("GOOGLE_API_KEY:", os.getenv("GOOGLE_API_KEY"))
configure(api_key=os.getenv("GOOGLE_API_KEY"))

nlp = spacy.load("en_core_web_sm")
# Use only genai.generate_content for Gemini API calls

app = FastAPI()

with open(os.path.join(os.path.dirname(__file__), "skills_list.txt")) as f:
    KNOWN_SKILLS = set(line.strip().lower() for line in f if line.strip())

class JobDescription(BaseModel):
    id: Optional[str] = None
    jobId: Optional[str] = None
    title: Optional[str] = None
    companyName: Optional[str] = None
    location: Optional[str] = None
    via: Optional[str] = None
    shareLink: Optional[str] = None
    postedAt: Optional[str] = None
    salary: Optional[str] = None
    scheduleType: Optional[str] = None
    qualifications: Optional[str] = None
    description: Optional[str] = None
    responsibilities: Optional[List[str]] = None
    benefits: Optional[List[str]] = None
    applyLinks: Optional[str] = None
    createdDateTime: Optional[str] = None
    lastUpdatedDateTime: Optional[str] = None
    # jobTitle and city are objects, not used for skill extraction

class ResumeAnalysisRequest(BaseModel):
    resume_text: str
    jobs: List[JobDescription]

class JobMatchResult(BaseModel):
    job_id: str
    job_title: str
    company: str
    match_percentage: float
    matched_skills: List[str]
    missing_skills: List[str]
    missing_certifications: List[str]
    missing_education: Optional[str]
    ai_suggestions: str
    location: Optional[str] = None
    salary: Optional[str] = None
    scheduleType: Optional[str] = None
    qualifications: Optional[str] = None
    description: Optional[str] = None
    responsibilities: Optional[List[str]] = None
    benefits: Optional[List[str]] = None
    applyLink: Optional[str] = None

class ResumeAnalysisResponse(BaseModel):
    top_matches: List[JobMatchResult]

def extract_skills(text: str) -> set:
    doc = nlp(text)
    found = set()
    for token in doc:
        if token.text.lower() in KNOWN_SKILLS:
            found.add(token.text.lower())
    return found

def extract_job_skills(job: dict) -> set:
    # Combine qualifications, description, and responsibilities for skill extraction
    text = (job.get("qualifications") or "") + " " + (job.get("description") or "")
    responsibilities = job.get("responsibilities")
    if responsibilities and isinstance(responsibilities, list):
        text += " " + " ".join(responsibilities)
    return extract_skills(text)

def call_gemini_suggestions(job, missing_skills, missing_qualifications, missing_experience):
    prompt = (
        f"A candidate is applying for the job '{job.get('title', '')}' at '{job.get('companyName', '')}'.\n"
        f"Their resume is missing these skills: {', '.join(missing_skills) or 'None'}, "
        f"qualifications: {missing_qualifications or 'None'}, "
        f"and experience: {missing_experience or 'None'}.\n"
        "Suggest what they should add or improve in their resume to reach a 100% match for this job."
    )
    model = GenerativeModel("models/gemini-1.5-flash")
    response = model.generate_content(prompt)
    print("Prompt sent to Gemini:", prompt)
    print("Gemini suggestion:", response.text if hasattr(response, 'text') else response)
    return response.text.strip() if hasattr(response, 'text') and response.text else str(response)

@app.post("/analyze", response_model=ResumeAnalysisResponse)
def analyze_resume(request: ResumeAnalysisRequest):
    resume_skills = extract_skills(request.resume_text)
    results = []

    for job in request.jobs:
        job_dict = job.dict() if hasattr(job, 'dict') else dict(job)
        job_skills = extract_job_skills(job_dict)
        matched_skills = list(resume_skills & job_skills)
        missing_skills = list(job_skills - resume_skills)

        # Basic extraction for missing qualifications and experience
        missing_qualifications = job_dict.get('qualifications', None)
        missing_experience = job_dict.get('description', None)  # You can improve this logic

        match_percentage = 100 * len(matched_skills) / max(1, len(job_skills)) if job_skills else 0.0

        ai_suggestions = None
        if match_percentage < 100:
            ai_suggestions = call_gemini_suggestions(
                job_dict, missing_skills, missing_qualifications, missing_experience
            )

        results.append(JobMatchResult(
            job_id=job_dict.get('id') or job_dict.get('jobId', ''),
            job_title=job_dict.get('title', ''),
            company=job_dict.get('companyName', ''),
            match_percentage=round(match_percentage, 2),
            matched_skills=matched_skills,
            missing_skills=missing_skills,
            missing_certifications=[],  # Add if you extract these
            missing_education=None,     # Add if you extract these
            ai_suggestions=ai_suggestions or "",
            location=job_dict.get('location'),
            salary=job_dict.get('salary'),
            scheduleType=job_dict.get('scheduleType'),
            qualifications=job_dict.get('qualifications'),
            description=job_dict.get('description'),
            responsibilities=job_dict.get('responsibilities'),
            benefits=job_dict.get('benefits'),
            applyLink=job_dict.get('applyLinks') or job_dict.get('applyLink'),
        ))

    results.sort(key=lambda x: x.match_percentage, reverse=True)
    return ResumeAnalysisResponse(top_matches=results[:5]) 