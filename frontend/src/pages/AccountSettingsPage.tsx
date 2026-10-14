import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/authApi';
import { User, Lock, Trash2, CheckCircle2, AlertCircle, ShieldAlert } from 'lucide-react';

const passwordSchema = z
  .object({
    currentPassword: z.string().min(1, 'Current password is required'),
    newPassword: z.string().min(8, 'New password must be at least 8 characters'),
    confirmPassword: z.string().min(1, 'Please confirm your new password'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'New passwords do not match',
    path: ['confirmPassword'],
  });

type PasswordFormData = z.infer<typeof passwordSchema>;

export const AccountSettingsPage: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<PasswordFormData>({
    resolver: zodResolver(passwordSchema),
  });

  const onChangePassword = async (data: PasswordFormData) => {
    setPasswordSuccess(null);
    setPasswordError(null);
    try {
      await authApi.changePassword({
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
      });
      setPasswordSuccess('Password successfully updated. Other active sessions were invalidated.');
      reset();
    } catch (err: unknown) {
      if (err instanceof Error) {
        setPasswordError(err.message);
      } else {
        setPasswordError('Failed to change password. Please check your current password.');
      }
    }
  };

  const handleDeleteAccount = async () => {
    setIsDeleting(true);
    try {
      await authApi.deleteAccount();
      await logout();
      navigate('/login');
    } catch (err) {
      setIsDeleting(false);
      setShowDeleteModal(false);
      alert('Failed to delete account. Please try again.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <div>
        <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Account Settings</h1>
        <p className="mt-1 text-sm text-slate-500">
          Manage your profile credentials and security preferences.
        </p>
      </div>

      {/* Account Info Card */}
      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
        <div className="flex items-center space-x-3 text-slate-900 font-semibold mb-6">
          <div className="p-2 bg-indigo-50 text-indigo-600 rounded-lg">
            <User className="w-5 h-5" />
          </div>
          <h2 className="text-lg">Profile Details</h2>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 text-sm">
          <div>
            <span className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1">
              Full Name
            </span>
            <span className="font-medium text-slate-900 text-base">{user?.name}</span>
          </div>

          <div>
            <span className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1">
              Email Address
            </span>
            <span className="font-medium text-slate-900 text-base">{user?.email}</span>
          </div>

          <div>
            <span className="block text-xs font-semibold uppercase tracking-wider text-slate-400 mb-1">
              Member Since
            </span>
            <span className="text-slate-600">
              {user?.createdAt
                ? new Date(user.createdAt).toLocaleDateString(undefined, {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                  })
                : 'N/A'}
            </span>
          </div>
        </div>
      </div>

      {/* Change Password Card */}
      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-xs">
        <div className="flex items-center space-x-3 text-slate-900 font-semibold mb-6">
          <div className="p-2 bg-amber-50 text-amber-600 rounded-lg">
            <Lock className="w-5 h-5" />
          </div>
          <h2 className="text-lg">Change Password</h2>
        </div>

        {passwordSuccess && (
          <div className="mb-6 p-4 rounded-xl bg-emerald-50 border border-emerald-200 flex items-center space-x-3 text-emerald-800 text-sm">
            <CheckCircle2 className="w-5 h-5 flex-shrink-0" />
            <span>{passwordSuccess}</span>
          </div>
        )}

        {passwordError && (
          <div className="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 flex items-center space-x-3 text-rose-700 text-sm">
            <AlertCircle className="w-5 h-5 flex-shrink-0" />
            <span>{passwordError}</span>
          </div>
        )}

        <form onSubmit={handleSubmit(onChangePassword)} className="space-y-4 max-w-md">
          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Current Password</label>
            <input
              type="password"
              {...register('currentPassword')}
              className="w-full px-3.5 py-2 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-600 focus:border-indigo-600"
              placeholder="••••••••"
            />
            {errors.currentPassword && (
              <p className="mt-1 text-xs text-rose-600">{errors.currentPassword.message}</p>
            )}
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">New Password</label>
            <input
              type="password"
              {...register('newPassword')}
              className="w-full px-3.5 py-2 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-600 focus:border-indigo-600"
              placeholder="At least 8 characters"
            />
            {errors.newPassword && (
              <p className="mt-1 text-xs text-rose-600">{errors.newPassword.message}</p>
            )}
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-700 mb-1">Confirm New Password</label>
            <input
              type="password"
              {...register('confirmPassword')}
              className="w-full px-3.5 py-2 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-600 focus:border-indigo-600"
              placeholder="Re-enter new password"
            />
            {errors.confirmPassword && (
              <p className="mt-1 text-xs text-rose-600">{errors.confirmPassword.message}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 transition-colors shadow-xs"
          >
            {isSubmitting ? 'Updating...' : 'Update Password'}
          </button>
        </form>
      </div>

      {/* Danger Zone: Account Deletion */}
      <div className="bg-rose-50/50 p-6 rounded-xl border border-rose-200">
        <div className="flex items-center space-x-3 text-rose-900 font-semibold mb-2">
          <div className="p-2 bg-rose-100 text-rose-600 rounded-lg">
            <Trash2 className="w-5 h-5" />
          </div>
          <h2 className="text-lg">Delete Account & Stored Data</h2>
        </div>

        <p className="text-xs sm:text-sm text-rose-700 mb-6 leading-relaxed">
          Permanently delete your account, authentication credentials, uploaded resume files, extracted texts, and all generated analysis reports. This operation cannot be reversed.
        </p>

        <button
          onClick={() => setShowDeleteModal(true)}
          className="inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold text-white bg-rose-600 hover:bg-rose-700 transition-colors shadow-xs"
        >
          <Trash2 className="w-4 h-4 mr-1.5" />
          <span>Delete My Account</span>
        </button>
      </div>

      {/* Delete Confirmation Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-xl border border-slate-200">
            <div className="w-12 h-12 bg-rose-100 text-rose-600 rounded-full flex items-center justify-center mb-4">
              <ShieldAlert className="w-6 h-6" />
            </div>
            <h3 className="text-lg font-bold text-slate-900 mb-2">Confirm Permanent Deletion?</h3>
            <p className="text-sm text-slate-600 mb-6 leading-relaxed">
              Are you completely sure? All your resumes, previous evaluations, and personal data will be immediately and irrevocably wiped from the database.
            </p>
            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="px-4 py-2 rounded-lg text-sm font-semibold text-slate-700 hover:bg-slate-100 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleDeleteAccount}
                disabled={isDeleting}
                className="px-4 py-2 rounded-lg text-sm font-semibold text-white bg-rose-600 hover:bg-rose-700 shadow-sm transition-colors"
              >
                {isDeleting ? 'Deleting Everything...' : 'Yes, Delete My Account'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
