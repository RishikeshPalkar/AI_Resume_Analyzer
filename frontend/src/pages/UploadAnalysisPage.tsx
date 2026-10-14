import React, { useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { resumeApi } from '../api/resumeApi';
import { analysisApi } from '../api/analysisApi';
import { UploadCloud, CheckCircle, AlertCircle, Sparkles, Building, Briefcase } from 'lucide-react';

export const UploadAnalysisPage: React.FC = () => {
  const navigate = useNavigate();

  // Form states
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [jobTitle, setJobTitle] = useState('');
  const [company, setCompany] = useState('');
  const [jobDescription, setJobDescription] = useState('');

  // Status & Progress states
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setErrorMessage(null);
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      if (file.size > 5 * 1024 * 1024) {
        setErrorMessage('File size exceeds the 5 MB limit.');
        return;
      }
      const allowed = ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      const ext = file.name.toLowerCase();
      if (!allowed.includes(file.type) && !ext.endsWith('.pdf') && !ext.endsWith('.docx')) {
        setErrorMessage('Invalid file format. Only PDF and DOCX files are supported.');
        return;
      }
      setSelectedFile(file);
    }
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setErrorMessage(null);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const file = e.dataTransfer.files[0];
      if (file.size > 5 * 1024 * 1024) {
        setErrorMessage('File size exceeds the 5 MB limit.');
        return;
      }
      const ext = file.name.toLowerCase();
      if (!ext.endsWith('.pdf') && !ext.endsWith('.docx')) {
        setErrorMessage('Invalid file format. Only PDF and DOCX files are supported.');
        return;
      }
      setSelectedFile(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!selectedFile) {
      setErrorMessage('Please upload a resume in PDF or DOCX format.');
      return;
    }

    if (!jobDescription || jobDescription.trim().length < 40) {
      setErrorMessage('Please provide a job description of at least 40 characters.');
      return;
    }

    setIsSubmitting(true);
    setStatusMessage('Uploading and parsing document with Apache PDFBox/POI...');

    try {
      // Step 1: Upload and extract resume text
      const uploadedResume = await resumeApi.uploadResume(selectedFile);

      // Step 2: Trigger AI requirement extraction and deterministic scoring
      setStatusMessage('Extracting technical requirements and calculating deterministic match score...');
      const analysisResult = await analysisApi.createAnalysis({
        resumeId: uploadedResume.id,
        jobTitle: jobTitle.trim() || undefined,
        company: company.trim() || undefined,
        jobDescriptionText: jobDescription.trim(),
      });

      setStatusMessage('Report generated successfully! Navigating...');
      navigate(`/analyses/${analysisResult.id}`);
    } catch (err: unknown) {
      setIsSubmitting(false);
      setStatusMessage(null);
      if (err instanceof Error) {
        setErrorMessage(err.message);
      } else {
        setErrorMessage('An error occurred during resume analysis. Please try again.');
      }
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="mb-8">
        <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">New Resume Match & Analysis</h1>
        <p className="mt-2 text-sm text-slate-600">
          Upload your candidate resume and compare it against the job description to get an algorithmic match score.
        </p>
      </div>

      {errorMessage && (
        <div className="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 flex items-start space-x-3 text-rose-700 text-sm">
          <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
          <span>{errorMessage}</span>
        </div>
      )}

      {isSubmitting && (
        <div className="mb-6 p-5 rounded-xl bg-indigo-50 border border-indigo-200 flex items-center space-x-4">
          <div className="w-6 h-6 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin flex-shrink-0"></div>
          <div>
            <h4 className="text-sm font-semibold text-indigo-900">Analysis In Progress</h4>
            <p className="text-xs text-indigo-700 mt-0.5">{statusMessage}</p>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Resume File Upload Box */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
          <label className="block text-sm font-semibold text-slate-900 mb-2">
            1. Upload Resume Document (PDF or DOCX)
          </label>
          <p className="text-xs text-slate-500 mb-4">
            Supports selectable PDF (.pdf) and Microsoft Word (.docx) up to 5 MB.
          </p>

          <div
            onDragOver={(e) => e.preventDefault()}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            className={`border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-colors ${
              selectedFile ? 'border-emerald-400 bg-emerald-50/30' : 'border-slate-300 hover:border-indigo-500 bg-slate-50/50'
            }`}
          >
            <input
              type="file"
              ref={fileInputRef}
              onChange={handleFileChange}
              accept=".pdf,.docx,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
              className="hidden"
            />

            {selectedFile ? (
              <div className="flex flex-col items-center">
                <div className="w-12 h-12 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mb-2">
                  <CheckCircle className="w-6 h-6" />
                </div>
                <span className="text-sm font-semibold text-slate-900">{selectedFile.name}</span>
                <span className="text-xs text-slate-500 mt-1">
                  {(selectedFile.size / (1024 * 1024)).toFixed(2)} MB • Click to replace file
                </span>
              </div>
            ) : (
              <div className="flex flex-col items-center">
                <div className="w-12 h-12 bg-indigo-50 text-indigo-600 rounded-full flex items-center justify-center mb-2">
                  <UploadCloud className="w-6 h-6" />
                </div>
                <span className="text-sm font-semibold text-slate-900">
                  Click to browse or drag and drop your file here
                </span>
                <span className="text-xs text-slate-500 mt-1">PDF or DOCX (Max 5 MB)</span>
              </div>
            )}
          </div>
        </div>

        {/* Job Details Section */}
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs space-y-4">
          <label className="block text-sm font-semibold text-slate-900">
            2. Target Job Information
          </label>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1 flex items-center">
                <Briefcase className="w-3.5 h-3.5 mr-1 text-slate-400" />
                Job Title (Optional)
              </label>
              <input
                type="text"
                value={jobTitle}
                onChange={(e) => setJobTitle(e.target.value)}
                placeholder="e.g. Senior Java Backend Developer"
                className="w-full px-3.5 py-2 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-600 focus:border-indigo-600"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1 flex items-center">
                <Building className="w-3.5 h-3.5 mr-1 text-slate-400" />
                Company Name (Optional)
              </label>
              <input
                type="text"
                value={company}
                onChange={(e) => setCompany(e.target.value)}
                placeholder="e.g. LTIMindtree"
                className="w-full px-3.5 py-2 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-600 focus:border-indigo-600"
              />
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="block text-xs font-medium text-slate-600">
                Job Description Text <span className="text-rose-500">*</span>
              </label>
              <span className="text-xs text-slate-400">
                {jobDescription.length} / 30,000 characters
              </span>
            </div>
            <textarea
              rows={8}
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
              placeholder="Paste the complete job description, required skills, preferred qualifications, and responsibilities here..."
              className="w-full px-3.5 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-600 focus:border-indigo-600 font-mono text-xs leading-relaxed"
            />
          </div>
        </div>

        {/* Submit Button */}
        <div className="flex justify-end">
          <button
            type="submit"
            disabled={isSubmitting || !selectedFile || jobDescription.length < 40}
            className="inline-flex items-center px-6 py-3 rounded-xl text-base font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-sm disabled:opacity-50 transition-colors cursor-pointer"
          >
            {isSubmitting ? (
              <>
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"></div>
                <span>Analyzing Resume...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-4 h-4 mr-2" />
                <span>Run Match Analysis</span>
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
