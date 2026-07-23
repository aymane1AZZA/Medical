import { CircleCheck } from 'lucide-react';

export default function PermissionGrid({ items = [] }) {
  return (
    <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
      {items.map((item) => (
        <article key={item} className="flex min-h-20 items-start gap-3 rounded-lg border border-slate-200 bg-white p-4 shadow-sm transition hover:border-teal-200 hover:shadow-md">
          <span className="grid h-8 w-8 shrink-0 place-items-center rounded-lg bg-teal-50">
            <CircleCheck className="size-4 text-primary" aria-hidden="true" />
          </span>
          <p className="text-sm font-semibold leading-6 text-slate-700">{item}</p>
        </article>
      ))}
    </div>
  );
}
