import { yupResolver } from '@hookform/resolvers/yup';
import { motion } from 'framer-motion';
import { useForm } from 'react-hook-form';
import { FiAlertCircle, FiArrowRight, FiClock, FiLogIn, FiMail, FiShield } from 'react-icons/fi';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import * as yup from 'yup';
import ChuLogo from '../components/ChuLogo.jsx';
import Loader from '../components/Loader.jsx';
import PasswordInput from '../components/PasswordInput.jsx';
import RoleSelector from '../components/RoleSelector.jsx';
import { useAuth } from '../contexts/AuthContext.jsx';
import { rolePath } from '../utils/roles';

const schema = yup.object({
  role: yup.string().required('Sélection obligatoire'),
  identifier: yup.string().required("L'email ou le nom d'utilisateur est obligatoire."),
  password: yup.string().required('Le mot de passe est obligatoire.'),
});

export default function LoginPage() {
  const navigate = useNavigate();
  const { signIn, isAuthenticated, user } = useAuth();
  const {
    register,
    handleSubmit,
    setError,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: yupResolver(schema),
    defaultValues: {
      role: '',
      identifier: '',
      password: '',
    },
  });

  if (isAuthenticated && user) {
    return <Navigate to={rolePath(user.role)} replace />;
  }

  const selectedRole = watch('role');

  const onSubmit = async (values) => {
    try {
      const loggedUser = await signIn(values);
      navigate(rolePath(loggedUser.role), { replace: true });
    } catch (exception) {
      const message = exception.response?.data?.message || 'Connexion impossible.';
      setError('root', { message });
    }
  };

  return (
    <main className="min-h-screen bg-[#f3f7f6]">
      <div className="grid min-h-screen lg:grid-cols-[0.9fr_1.1fr]">
        <section className="clinical-grid relative hidden overflow-hidden bg-chu-ink px-10 py-10 text-white lg:flex lg:flex-col lg:justify-between">
          <div className="absolute inset-x-0 top-0 h-1 bg-gradient-to-r from-teal-300 via-sky-300 to-red-300" />
          <ChuLogo inverse />

          <div className="max-w-xl">
            <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3 py-1.5 text-xs font-bold uppercase tracking-[0.16em] text-teal-100">
              <FiShield aria-hidden="true" />
              Portail CHU
            </div>
            <h2 className="mt-6 text-5xl font-bold leading-tight">Système d'information hospitalier</h2>
            <p className="mt-5 max-w-lg text-base leading-7 text-slate-300">
              Un accès clair par profil pour orienter chaque utilisateur vers son espace opérationnel.
            </p>

            <div className="mt-10 grid grid-cols-3 gap-3">
              {[
                ['08:30', 'Relève médicale'],
                ['24/7', 'Continuité SIH'],
                ['6', 'Profils actifs'],
              ].map(([value, label]) => (
                <div key={label} className="rounded-lg border border-white/15 bg-white/10 p-4 backdrop-blur">
                  <p className="text-2xl font-bold text-white">{value}</p>
                  <p className="mt-1 text-xs font-semibold uppercase tracking-[0.12em] text-slate-300">{label}</p>
                </div>
              ))}
            </div>
          </div>

          <div className="rounded-lg border border-white/15 bg-white/10 p-4 backdrop-blur">
            <div className="flex items-center gap-3">
              <span className="grid h-10 w-10 place-items-center rounded-lg bg-white/15 text-teal-100">
                <FiClock aria-hidden="true" />
              </span>
              <div>
                <p className="text-sm font-bold text-white">Session professionnelle</p>
                <p className="text-sm text-slate-300">Sélection du profil requise avant connexion.</p>
              </div>
            </div>
          </div>
        </section>

        <section className="flex items-center justify-center px-4 py-8 sm:px-6 lg:px-10">
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.35 }}
            className="form-surface w-full max-w-3xl rounded-lg p-5 shadow-panel ring-1 ring-slate-200 sm:p-8"
          >
            <div className="mb-8 flex flex-col gap-5 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <ChuLogo />
                <div className="mt-7">
                  <p className="text-sm font-bold uppercase tracking-[0.16em] text-chu-teal">Connexion</p>
                  <h1 className="mt-2 text-3xl font-bold text-chu-ink">Bienvenue dans votre espace CHU</h1>
                </div>
              </div>
              <span className="inline-flex w-fit items-center gap-2 rounded-full bg-slate-100 px-3 py-1.5 text-xs font-bold uppercase tracking-[0.12em] text-slate-600">
                <FiShield aria-hidden="true" />
                Accès contrôlé
              </span>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
              <RoleSelector value={selectedRole} onChange={(role) => setValue('role', role, { shouldValidate: true })} error={errors.role} />

              <div>
                <label className="mb-2 block text-sm font-semibold text-slate-700" htmlFor="identifier">
                  Email ou nom d'utilisateur
                </label>
                <div className="relative">
                  <FiMail className="pointer-events-none absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400" aria-hidden="true" />
                  <input
                    id="identifier"
                    type="text"
                    autoComplete="username"
                    className="focus-ring h-12 w-full rounded-lg border border-slate-300 bg-white px-10 text-slate-900 shadow-sm placeholder:text-slate-400"
                    placeholder="admin@chu.local"
                    {...register('identifier')}
                  />
                </div>
                {errors.identifier && <p className="mt-2 text-sm font-medium text-chu-red">{errors.identifier.message}</p>}
              </div>

              <PasswordInput register={register} error={errors.password} />

              <div className="flex items-center justify-between gap-4">
                <Link to="/mot-de-passe-oublie" className="text-sm font-semibold text-chu-teal hover:text-chu-emerald">
                  Mot de passe oublié
                </Link>
              </div>

              {errors.root && (
                <div className="flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-3 text-sm font-semibold text-chu-red">
                  <FiAlertCircle className="mt-0.5 h-5 w-5 shrink-0" aria-hidden="true" />
                  <span>{errors.root.message}</span>
                </div>
              )}

              <button
                type="submit"
                disabled={isSubmitting}
                className="focus-ring inline-flex h-12 w-full items-center justify-center gap-2 rounded-lg bg-chu-teal px-4 text-sm font-bold text-white shadow-lg shadow-teal-900/15 transition hover:-translate-y-0.5 hover:bg-chu-emerald disabled:cursor-not-allowed disabled:opacity-70 disabled:hover:translate-y-0"
              >
                {isSubmitting ? <Loader label="Connexion" inverse /> : <><FiLogIn aria-hidden="true" /> Connexion <FiArrowRight aria-hidden="true" /></>}
              </button>
            </form>
          </motion.div>
        </section>
      </div>
    </main>
  );
}
