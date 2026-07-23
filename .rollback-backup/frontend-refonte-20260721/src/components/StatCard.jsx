export default function StatCard({ label, value, icon: Icon, tone = 'text-chu-teal' }) {
  return (
    <article className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
      <div className="flex items-center justify-between gap-3">
        <p className="text-sm font-semibold text-slate-500">{label}</p>
        {Icon && (
          <span className="grid h-9 w-9 place-items-center rounded-lg bg-slate-50">
            <Icon className={`h-5 w-5 ${tone}`} aria-hidden="true" />
          </span>
        )}
      </div>
      <p className="mt-3 text-2xl font-bold text-chu-ink">{value}</p>
    </article>
  );
}
