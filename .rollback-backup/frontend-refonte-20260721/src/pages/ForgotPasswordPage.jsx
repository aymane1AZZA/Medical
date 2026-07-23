import { Link } from 'react-router-dom';
import ChuLogo from '../components/ChuLogo.jsx';

export default function ForgotPasswordPage() {
  return (
    <main className="grid min-h-screen place-items-center bg-slate-50 px-4">
      <section className="w-full max-w-md rounded-lg border border-slate-200 bg-white p-6 shadow-panel">
        <ChuLogo />
        <h1 className="mt-8 text-2xl font-bold text-chu-ink">Mot de passe oublié</h1>
        <p className="mt-3 text-sm leading-6 text-slate-600">
          Contactez l'administrateur du CHU pour réinitialiser votre mot de passe. Les comptes sont créés et maintenus uniquement par l'administration.
        </p>
        <Link
          to="/login"
          className="focus-ring mt-6 inline-flex h-11 items-center justify-center rounded-lg bg-chu-teal px-4 text-sm font-bold text-white hover:bg-chu-emerald"
        >
          Retour à la connexion
        </Link>
      </section>
    </main>
  );
}
