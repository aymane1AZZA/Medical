import { useState } from 'react';
import { FiEye, FiEyeOff, FiLock } from 'react-icons/fi';

export default function PasswordInput({ register, error }) {
  const [visible, setVisible] = useState(false);

  return (
    <div>
      <label className="mb-2 block text-sm font-semibold text-slate-700" htmlFor="password">
        Mot de passe
      </label>
      <div className="relative">
        <FiLock className="pointer-events-none absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400" />
        <input
          id="password"
          type={visible ? 'text' : 'password'}
          autoComplete="current-password"
          className="focus-ring h-12 w-full rounded-lg border border-slate-300 bg-white px-10 text-slate-900 shadow-sm placeholder:text-slate-400"
          placeholder="Votre mot de passe"
          {...register('password')}
        />
        <button
          type="button"
          onClick={() => setVisible((current) => !current)}
          className="focus-ring absolute right-2 top-1/2 grid h-8 w-8 -translate-y-1/2 place-items-center rounded-md text-slate-500 hover:bg-slate-100 hover:text-slate-800"
          aria-label={visible ? 'Masquer le mot de passe' : 'Afficher le mot de passe'}
          title={visible ? 'Masquer le mot de passe' : 'Afficher le mot de passe'}
        >
          {visible ? <FiEyeOff aria-hidden="true" /> : <FiEye aria-hidden="true" />}
        </button>
      </div>
      {error && <p className="mt-2 text-sm font-medium text-chu-red">{error.message}</p>}
    </div>
  );
}
