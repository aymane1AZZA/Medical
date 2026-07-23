import { FaHospitalSymbol } from 'react-icons/fa';

export default function ChuLogo({ compact = false, inverse = false }) {
  return (
    <div className="flex items-center gap-3">
      <div className={`grid h-12 w-12 shrink-0 place-items-center rounded-lg bg-white text-chu-teal shadow-md ring-1 ${inverse ? 'ring-white/20' : 'ring-slate-200'}`}>
        <FaHospitalSymbol className="h-7 w-7" aria-hidden="true" />
      </div>
      {!compact && (
        <div>
          <p className={`text-sm font-semibold uppercase tracking-[0.18em] ${inverse ? 'text-teal-100' : 'text-chu-teal'}`}>CHU</p>
          <h1 className={`text-xl font-bold ${inverse ? 'text-white' : 'text-chu-ink'}`}>SIH Auth</h1>
        </div>
      )}
    </div>
  );
}
