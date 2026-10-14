export interface UserProfile {
  id: number;
  name: string;
  email: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
  user: UserProfile;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface Resume {
  id: number;
  originalFilename: string;
  detectedContentType: string;
  characterCount: number;
  createdAt: string;
}

export interface ResumeDetail extends Resume {
  extractedText: string;
}

export interface JobDescription {
  id: number;
  title?: string;
  company?: string;
  descriptionText: string;
  createdAt: string;
}

export interface SkillAssessment {
  skillName: string;
  category: string;
  importance: 'REQUIRED' | 'PREFERRED';
  assessmentStatus: 'MATCHED' | 'PARTIAL' | 'MISSING';
  evidence: string;
  explanation: string;
}

export interface KeywordMatch {
  keyword: string;
  foundInResume: boolean;
  context: string;
}

export interface EvidenceItem {
  topic: string;
  quoteOrContext: string;
  status: 'SUPPORTED' | 'NOT_FOUND' | 'PARTIALLY_SUPPORTED';
}

export interface ScoreBreakdown {
  overallScore: number;
  requiredSkillsScore: number;
  experienceEvidenceScore: number;
  qualificationsScore: number;
  preferredSkillsScore: number;
  requiredSkillsTotal: number;
  requiredSkillsMatched: number;
  preferredSkillsTotal: number;
  preferredSkillsMatched: number;
  formulaExplanation: string;
}

export interface AnalysisSummary {
  id: number;
  resumeId: number;
  resumeFilename: string;
  jobDescriptionId: number;
  jobTitle?: string;
  company?: string;
  matchScore: number;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  createdAt: string;
}

export interface AnalysisDetail {
  id: number;
  resumeId: number;
  resumeFilename: string;
  jobDescriptionId: number;
  jobTitle?: string;
  company?: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  matchScore: number;
  scoreBreakdown?: ScoreBreakdown;
  summary: string;
  matchingSkills: string[];
  missingRequiredSkills: string[];
  missingPreferredSkills: string[];
  strengths: string[];
  recommendations: string[];
  keywordAnalysis: KeywordMatch[];
  evidence: EvidenceItem[];
  skillAssessments: SkillAssessment[];
  modelIdentifier?: string;
  promptVersion?: string;
  createdAt: string;
  completedAt?: string;
  disclaimer: string;
}

export interface DashboardStats {
  totalAnalyses: number;
}
