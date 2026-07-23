import { useState } from 'react'
import { Eye, EyeOff, LockKeyhole } from 'lucide-react'
import { Field, FieldError, FieldLabel } from '@/components/ui/field'
import { InputGroup, InputGroupAddon, InputGroupButton, InputGroupInput } from '@/components/ui/input-group'

export default function PasswordInput({ register, error }) {
  const [visible, setVisible] = useState(false)
  return <Field data-invalid={Boolean(error)}><FieldLabel htmlFor="password">Mot de passe</FieldLabel><InputGroup className="h-10"><InputGroupAddon><LockKeyhole /></InputGroupAddon><InputGroupInput id="password" type={visible ? 'text' : 'password'} autoComplete="current-password" placeholder="Votre mot de passe" aria-invalid={Boolean(error)} {...register('password')} /><InputGroupAddon align="inline-end"><InputGroupButton size="icon-xs" onClick={() => setVisible((current) => !current)} aria-label={visible ? 'Masquer le mot de passe' : 'Afficher le mot de passe'}>{visible ? <EyeOff /> : <Eye />}</InputGroupButton></InputGroupAddon></InputGroup>{error && <FieldError>{error.message}</FieldError>}</Field>
}
