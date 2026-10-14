import React from 'react';
import { Link } from 'react-router-dom';
import { FileCheck, Sparkles, Shield, Cpu, Target, ArrowRight, BarChart3, CheckCircle } from 'lucide-react';

export const LandingPage: React.FC = () => {
  return (
    <div className="flex flex-col min-h-[calc(100vh-4rem)]">
      {/* Hero Section */}
      <section className="relative overflow-hidden py-16 sm:py-24 bg-gradient-to-b from-indigo-50/50 via-white to-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-indigo-100 text-indigo-700 text-xs font-semibold uppercase tracking-wider mb-6">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Built with Spring Boot 3.4 & Spring AI</span>
          </div>

          <h1 className="text-4xl sm:text-6xl font-extrabold text-slate-900 tracking-tight max-w-4xl mx-auto leading-tight">
            Optimize Your Resume for Tech Roles with <span className="text-indigo-600">Deterministic AI</span>
          </h1>

          <p className="mt-6 text-lg sm:text-xl text-slate-600 max-w-2xl mx-auto leading-relaxed">
            Upload your resume, paste a target job description, and receive an objective competency breakdown, deterministic match score, and evidence-grounded suggestions.
          </p>

          <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link
              to="/register"
              className="w-full sm:w-auto inline-flex items-center justify-center px-6 py-3.5 rounded-lg text-base font-semibold text-white bg-indigo-600 hover:bg-indigo-700 shadow-sm transition-all hover:shadow"
            >
              <span>Get Started Free</span>
              <ArrowRight className="w-4 h-4 ml-2" />
            </Link>
            <Link
              to="/login"
              className="w-full sm:w-auto inline-flex items-center justify-center px-6 py-3.5 rounded-lg text-base font-semibold text-slate-700 bg-white border border-slate-300 hover:bg-slate-50 transition-colors"
            >
              Sign In to Your Account
            </Link>
          </div>

          <div className="mt-12 flex flex-wrap justify-center gap-6 text-xs text-slate-500 font-medium">
            <div className="flex items-center space-x-1.5">
              <CheckCircle className="w-4 h-4 text-emerald-600" />
              <span>Native PDF & DOCX Parsing</span>
            </div>
            <div className="flex items-center space-x-1.5">
              <CheckCircle className="w-4 h-4 text-emerald-600" />
              <span>Backend Deterministic Scoring</span>
            </div>
            <div className="flex items-center space-x-1.5">
              <CheckCircle className="w-4 h-4 text-emerald-600" />
              <span>Anti-Hallucination Guardrails</span>
            </div>
          </div>
        </div>
      </section>

      {/* How it Works */}
      <section className="py-16 bg-white border-y border-slate-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <h2 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">
              Engineering-Grade Evaluation Workflow
            </h2>
            <p className="mt-3 text-slate-600">
              Unlike generic ChatGPT prompts that invent numbers, our platform calculates scores algorithmically using weighted backend logic.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="p-6 bg-slate-50 rounded-xl border border-slate-200">
              <div className="w-10 h-10 rounded-lg bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold text-lg mb-4">
                1
              </div>
              <h3 className="text-lg font-semibold text-slate-900 mb-2">Secure Text Extraction</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Documents are safely processed using Apache PDFBox 3 and POI. Magic bytes and media types are strictly verified before parsing.
              </p>
            </div>

            <div className="p-6 bg-slate-50 rounded-xl border border-slate-200">
              <div className="w-10 h-10 rounded-lg bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold text-lg mb-4">
                2
              </div>
              <h3 className="text-lg font-semibold text-slate-900 mb-2">Spring AI Semantic Mapping</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Structured schema extraction isolates technical competencies, separating required qualifications from optional preferences without hallucination.
              </p>
            </div>

            <div className="p-6 bg-slate-50 rounded-xl border border-slate-200">
              <div className="w-10 h-10 rounded-lg bg-indigo-100 text-indigo-600 flex items-center justify-center font-bold text-lg mb-4">
                3
              </div>
              <h3 className="text-lg font-semibold text-slate-900 mb-2">Deterministic Scoring Engine</h3>
              <p className="text-sm text-slate-600 leading-relaxed">
                Java backend calculates weighted alignment (50% Required, 25% Experience, 15% Qualifications, 10% Preferred) for completely reproducible reports.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section className="py-16 bg-slate-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-12">
            <h2 className="text-2xl sm:text-3xl font-bold text-slate-900 tracking-tight">
              Purpose-Built for Technical Interviews
            </h2>
            <p className="mt-2 text-slate-600">
              Designed as a portfolio project demonstrating senior full-stack Java and Spring Boot architecture.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-xs">
              <Cpu className="w-6 h-6 text-indigo-600 mb-3" />
              <h4 className="font-semibold text-slate-900 mb-1">Java 21 & Spring Boot</h4>
              <p className="text-xs text-slate-600">Modular monolith, Spring Security with stateless JWT, and Flyway database migrations.</p>
            </div>

            <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-xs">
              <Target className="w-6 h-6 text-indigo-600 mb-3" />
              <h4 className="font-semibold text-slate-900 mb-1">Skills Gap Analysis</h4>
              <p className="text-xs text-slate-600">Identifies exact missing libraries, tools, and frameworks required by the employer.</p>
            </div>

            <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-xs">
              <BarChart3 className="w-6 h-6 text-indigo-600 mb-3" />
              <h4 className="font-semibold text-slate-900 mb-1">Score Breakdown</h4>
              <p className="text-xs text-slate-600">Transparent mathematical breakdown across all 4 weighted evaluation categories.</p>
            </div>

            <div className="bg-white p-6 rounded-lg border border-slate-200 shadow-xs">
              <Shield className="w-6 h-6 text-indigo-600 mb-3" />
              <h4 className="font-semibold text-slate-900 mb-1">User Data Privacy</h4>
              <p className="text-xs text-slate-600">One-click account and resume deletion. Strict cross-user data isolation.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Privacy Notice */}
      <section className="py-12 bg-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center bg-indigo-50/50 p-8 rounded-2xl border border-indigo-100">
          <FileCheck className="w-8 h-8 text-indigo-600 mx-auto mb-3" />
          <h3 className="text-lg font-semibold text-slate-900">Document Processing & Privacy Policy</h3>
          <p className="mt-2 text-sm text-slate-600 leading-relaxed">
            Your uploaded documents are processed securely in server memory and persisted strictly within your private database schema. Data sent to configured AI models is subject to standard provider API policies. You retain 100% control over your data with immediate permanent deletion rights.
          </p>
        </div>
      </section>
    </div>
  );
};
