import { motion } from 'framer-motion';
import { FiCheck } from 'react-icons/fi';
import { FaFlask, FaHospitalUser, FaUserNurse, FaUserShield } from 'react-icons/fa';
import { FaUserDoctor } from 'react-icons/fa6';
import { MdBiotech } from 'react-icons/md';
import { roles } from '../utils/roles';

const icons = {
  ROLE_ADMIN: FaUserShield,
  ROLE_MEDECIN: FaUserDoctor,
  ROLE_INFERMIER: FaUserNurse,
  ROLE_BIOMEDICAL: MdBiotech,
  ROLE_PATIENT: FaHospitalUser,
  ROLE_LABO: FaFlask,
};

export default function RoleSelector({ value, onChange, error }) {
  return (
    <div>
      <div className="mb-3 flex items-center justify-between gap-3">
        <label className="text-sm font-bold text-slate-800">Profil d'accès</label>
        {error && <span className="rounded-full bg-red-50 px-2.5 py-1 text-xs font-bold text-chu-red">{error.message}</span>}
      </div>
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {roles.map((role) => {
          const Icon = icons[role.value];
          const selected = value === role.value;
          return (
            <motion.button
              whileTap={{ scale: 0.98 }}
              type="button"
              key={role.value}
              onClick={() => onChange(role.value)}
              className={`focus-ring group relative flex min-h-24 items-start gap-3 rounded-lg border p-3 text-left transition ${
                selected ? 'border-chu-teal bg-teal-50 shadow-md shadow-teal-900/10' : 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50 hover:shadow-sm'
              }`}
              aria-pressed={selected}
            >
              <span className={`grid h-10 w-10 shrink-0 place-items-center rounded-lg bg-gradient-to-br ${role.accent} text-white shadow-sm`}>
                <Icon className="h-5 w-5" aria-hidden="true" />
              </span>
              <span className="min-w-0">
                <span className="block text-sm font-bold leading-5 text-slate-900">{role.label}</span>
                <span className="mt-1 block text-xs font-semibold leading-5 text-slate-500">{role.description}</span>
              </span>
              {selected && (
                <span className="absolute right-3 top-3 grid h-5 w-5 place-items-center rounded-full bg-chu-teal text-white">
                  <FiCheck className="h-3.5 w-3.5" aria-hidden="true" />
                </span>
              )}
            </motion.button>
          );
        })}
      </div>
    </div>
  );
}
