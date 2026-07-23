import { ImSpinner2 } from 'react-icons/im';

export default function Loader({ label = 'Chargement', fullScreen = false, inverse = false }) {
  const content = (
    <div className={`flex items-center justify-center gap-3 text-sm font-semibold ${inverse ? 'text-white' : 'text-chu-teal'}`}>
      <ImSpinner2 className="h-5 w-5 animate-spin" aria-hidden="true" />
      <span>{label}</span>
    </div>
  );

  if (!fullScreen) return content;

  return <main className="grid min-h-screen place-items-center bg-slate-50">{content}</main>;
}
