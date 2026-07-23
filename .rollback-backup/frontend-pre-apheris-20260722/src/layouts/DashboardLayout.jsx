import { FiLogOut, FiShield, FiUser } from 'react-icons/fi';
import ChuLogo from '../components/ChuLogo.jsx';
import { useAuth } from '../contexts/AuthContext.jsx';
import { roleLabel } from '../utils/roles';

export default function DashboardLayout({ children, title, eyebrow, actions }) {
  const { signOut, user } = useAuth();

  return (
    <main className="min-h-screen bg-slate-50">
      <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/95 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
          <ChuLogo />
          <div className="flex items-center gap-3">
            <div className="hidden items-center gap-2 rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 sm:flex">
              <FiUser className="h-4 w-4 text-chu-teal" aria-hidden="true" />
              <span className="text-sm font-bold text-slate-700">{roleLabel(user?.role)}</span>
            </div>
            <button
              type="button"
              onClick={signOut}
              className="focus-ring inline-flex h-10 items-center gap-2 rounded-lg bg-slate-900 px-3 text-sm font-semibold text-white hover:bg-slate-700"
            >
              <FiLogOut aria-hidden="true" />
              <span className="hidden sm:inline">Déconnexion</span>
            </button>
          </div>
        </div>
      </header>

      <section className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
        <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="inline-flex items-center gap-2 rounded-full bg-teal-50 px-3 py-1 text-xs font-bold uppercase tracking-[0.14em] text-chu-teal">
                <FiShield aria-hidden="true" />
                {eyebrow || roleLabel(user?.role)}
              </p>
              <h2 className="mt-3 text-3xl font-bold text-chu-ink">{title}</h2>
              <p className="mt-2 text-sm font-semibold text-slate-500">{user?.fullName}</p>
            </div>
            {actions}
          </div>
        </div>
        <div className="mt-6">{children}</div>
      </section>
    </main>
  );
}
