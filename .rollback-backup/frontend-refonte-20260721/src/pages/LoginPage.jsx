import { yupResolver } from '@hookform/resolvers/yup'
import { ArrowRight, ClipboardCheck, KeyRound, Mail, ShieldCheck, Stethoscope } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import * as yup from 'yup'
import ChuLogo from '@/components/ChuLogo'
import Loader from '@/components/Loader'
import PasswordInput from '@/components/PasswordInput'
import RoleSelector from '@/components/RoleSelector'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Field, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import { InputGroup, InputGroupAddon, InputGroupInput } from '@/components/ui/input-group'
import { useAuth } from '@/contexts/AuthContext'
import { rolePath } from '@/utils/roles'

const schema = yup.object({ role: yup.string().required('Sélection obligatoire'), identifier: yup.string().required("L’email ou le nom d’utilisateur est obligatoire."), password: yup.string().required('Le mot de passe est obligatoire.') })

export default function LoginPage() {
  const navigate = useNavigate()
  const { signIn, isAuthenticated, user } = useAuth()
  const { register, handleSubmit, setError, setValue, watch, formState: { errors, isSubmitting } } = useForm({ resolver: yupResolver(schema), defaultValues: { role: '', identifier: '', password: '' } })
  if (isAuthenticated && user) return <Navigate to={rolePath(user.role)} replace />
  const selectedRole = watch('role')
  const onSubmit = async (values) => { try { const loggedUser = await signIn(values); navigate(rolePath(loggedUser.role), { replace: true }) } catch (exception) { setError('root', { message: exception.response?.data?.message || 'Connexion impossible.' }) } }

  return <main className="grid min-h-screen bg-background lg:grid-cols-[minmax(0,0.85fr)_minmax(0,1.15fr)]">
    <section className="hidden flex-col justify-between border-r bg-sidebar p-10 text-sidebar-foreground lg:flex"><ChuLogo inverse /><div className="max-w-lg"><p className="text-sm font-semibold text-sidebar-primary">Unité d’aphérèse</p><h1 className="mt-3 text-4xl font-semibold leading-tight">Le parcours clinique, de l’éligibilité à la clôture.</h1><p className="mt-4 text-sm leading-6 text-sidebar-foreground/70">Un accès sécurisé aux décisions médicales, à la coordination des séances et à la surveillance en temps réel.</p><ol className="mt-8 flex flex-col gap-3">{[[Stethoscope, 'Évaluer', 'Dossier, bilans et éligibilité'], [ClipboardCheck, 'Prescrire', 'Protocole et objectifs thérapeutiques'], [ShieldCheck, 'Surveiller', 'Séance, alertes et traçabilité']].map(([Icon, title, detail], index) => <li key={title} className="grid grid-cols-[36px_1fr] gap-3 border-t border-sidebar-border pt-3"><span className="grid size-8 place-items-center rounded-lg bg-sidebar-accent text-sidebar-primary"><Icon className="size-4" /></span><div><p className="text-sm font-semibold">{index + 1}. {title}</p><p className="mt-1 text-xs text-sidebar-foreground/60">{detail}</p></div></li>)}</ol></div><p className="text-xs text-sidebar-foreground/50">Accès professionnel contrôlé · Journalisation des actions sensibles</p></section>
    <section className="flex min-w-0 items-center justify-center p-4 sm:p-8"><Card className="w-full min-w-0 max-w-xl"><CardHeader className="border-b"><div className="flex items-start justify-between gap-4"><div className="min-w-0"><div className="lg:hidden"><ChuLogo /></div><CardTitle className="mt-6 text-2xl lg:mt-0">Connexion à l’espace clinique</CardTitle><CardDescription className="mt-1">Sélectionnez votre profil puis utilisez vos identifiants CHU.</CardDescription></div><span className="hidden size-10 shrink-0 place-items-center rounded-lg bg-secondary text-primary sm:grid"><KeyRound className="size-5" /></span></div></CardHeader><CardContent className="pt-5"><form onSubmit={handleSubmit(onSubmit)}><FieldGroup><RoleSelector value={selectedRole} onChange={(role) => setValue('role', role, { shouldValidate: true })} error={errors.role} /><Field data-invalid={Boolean(errors.identifier)}><FieldLabel htmlFor="identifier">Email ou nom d’utilisateur</FieldLabel><InputGroup className="h-10"><InputGroupAddon><Mail /></InputGroupAddon><InputGroupInput id="identifier" autoComplete="username" placeholder="prenom.nom" aria-invalid={Boolean(errors.identifier)} {...register('identifier')} /></InputGroup>{errors.identifier && <FieldError>{errors.identifier.message}</FieldError>}</Field><PasswordInput register={register} error={errors.password} /><div className="flex justify-end"><Link to="/mot-de-passe-oublie" className="text-sm font-medium text-primary hover:underline">Mot de passe oublié</Link></div>{errors.root && <Alert variant="destructive"><ShieldCheck /><AlertTitle>Accès refusé</AlertTitle><AlertDescription>{errors.root.message}</AlertDescription></Alert>}<Button type="submit" size="lg" disabled={isSubmitting} className="w-full">{isSubmitting ? <Loader label="Connexion" inverse /> : <><span>Se connecter</span><ArrowRight data-icon="inline-end" /></>}</Button></FieldGroup></form></CardContent></Card></section>
  </main>
}
