'use client';

import type { ReactElement } from 'react';
import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useForm, type SubmitHandler } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useToast } from '@/hooks/use-toast';
import {
  registrationSchema,
  type RegistrationInput,
  type Role,
  roles,
  buyerSchema,
  supplierSchema,
  transporterSchema,
  driverSchema,
} from '@/lib/schemas';
// Note: Assuming registerUser is an action/function that handles the actual backend call.
// You would replace the mock implementation with your actual API call.
// import { registerUser } from '@/actions/auth';

// Mock registration function (replace with actual API call)
async function registerUser(data: RegistrationInput): Promise<{ success: boolean; message: string }> {
  console.log('Registering user:', data);
  // Simulate API call
  await new Promise(resolve => setTimeout(resolve, 1000));
  // Simulate success/failure
  if (data.email === 'fail@example.com') {
    return { success: false, message: 'Registration failed. Email already exists.' };
  }
  return { success: true, message: 'Registration successful!' };
}


export default function RegisterPage(): ReactElement {
  const [selectedRole, setSelectedRole] = useState<Role | ''>('');
  const [formKey, setFormKey] = useState(0); // Key to force form re-render on role change
  const { toast } = useToast();
  const router = useRouter();

  // Determine the schema based on the selected role.
  // This needs to be recalculated whenever selectedRole changes.
  const currentSchema =
    selectedRole === 'buyer'
      ? buyerSchema
      : selectedRole === 'supplier'
        ? supplierSchema
        : selectedRole === 'transporter'
          ? transporterSchema
          : selectedRole === 'driver'
            ? driverSchema
            : registrationSchema; // Fallback schema

  const form = useForm<RegistrationInput>({
    resolver: zodResolver(currentSchema),
    defaultValues: {
      email: '',
      phone: '',
      password: '',
      confirmPassword: '',
      role: selectedRole || undefined,
      // Role specific fields (initialize to empty or undefined)
      companyName: '',
      contactPerson: '',
      businessAddress: '',
      taxId: '',
      farmName: '',
      ownerName: '',
      produceTypes: '',
      physicalAddress: '',
      certificationDetails: '',
      officeAddress: '',
      fleetSize: undefined,
      licenseNumbers: '',
      fullName: '',
      homeAddress: '',
      licenseNumber: '',
      experienceYears: undefined,
      vehicleDetails: '',
    },
    mode: 'onChange', // Validate on change for better UX
  });

  const onSubmit: SubmitHandler<RegistrationInput> = async (data) => {
    try {
      const result = await registerUser(data);
      if (result.success) {
        toast({
          title: 'Registration Successful',
          description: result.message,
        });
        router.push('/login'); // Redirect to login after successful registration
      } else {
        toast({
          title: 'Registration Failed',
          description: result.message,
          variant: 'destructive',
        });
      }
    } catch (error) {
      console.error('Registration error:', error);
      toast({
        title: 'Registration Error',
        description: 'An unexpected error occurred. Please try again.',
        variant: 'destructive',
      });
    }
  };

  const handleRoleChange = (value: string): void => {
     const newRole = value as Role | '';
     setSelectedRole(newRole);
     // Reset form values when role changes to avoid carrying over invalid data
     // Keep common fields like email, phone, password if desired, but reset specifics
     const currentValues = form.getValues(); // Get current common values if needed
     form.reset({
        // Keep common fields if needed, or reset all for a clean slate
        email: '', // Resetting all for simplicity
        phone: '',
        password: '',
        confirmPassword: '',
        role: newRole || undefined, // Set the new role
        // Reset role-specific fields explicitly
        companyName: '',
        contactPerson: '',
        businessAddress: '',
        taxId: '',
        farmName: '',
        ownerName: '',
        produceTypes: '',
        physicalAddress: '',
        certificationDetails: '',
        officeAddress: '',
        fleetSize: undefined,
        licenseNumbers: '',
        fullName: '',
        homeAddress: '',
        licenseNumber: '',
        experienceYears: undefined,
        vehicleDetails: '',
     }, {
        // Keep form state like errors and touched status if desired, or reset them too
        // keepErrors: false,
        // keepDirty: false,
        // keepValues: false,
        // keepDefaultValues: false,
        // keepIsSubmitted: false,
        // keepTouched: false,
        // keepIsValid: false,
        // keepSubmitCount: false,
     });
     // Increment the key to force remounting the Form component and re-initializing useForm
     setFormKey(prevKey => prevKey + 1);
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-4 sm:p-8 md:p-12 lg:p-24 bg-background">
      <Card className="w-full max-w-lg shadow-lg">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-primary">
            Register
          </CardTitle>
          <CardDescription>
            Create your RoleConnector account. Select your role to begin.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {/* Use the formKey here to trigger re-render */}
          <Form {...form} key={formKey}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              {/* Role Selection */}
              <FormField
                control={form.control}
                name="role"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Select Your Role *</FormLabel>
                    <Select
                      onValueChange={(value) => {
                        // field.onChange(value); // RHF handles this via form.reset now
                        handleRoleChange(value); // Update local state and reset/remount form
                      }}
                      // Use selectedRole directly as value since form state is reset
                      value={selectedRole}
                      // defaultValue={field.value} // defaultValue might interfere with reset
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a role" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {roles.map((role) => (
                          <SelectItem key={role} value={role}>
                            {role.charAt(0).toUpperCase() + role.slice(1)}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Common Fields */}
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email *</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="your.email@example.com"
                        type="email"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="phone"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Phone Number *</FormLabel>
                    <FormControl>
                      <Input placeholder="+1234567890" type="tel" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password *</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="********"
                        type="password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="confirmPassword"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Confirm Password *</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="********"
                        type="password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />


              {/* Dynamic Role-Specific Fields */}
              {selectedRole === 'buyer' && (
                <>
                  <FormField
                    control={form.control}
                    name="companyName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Company Name *</FormLabel>
                        <FormControl>
                          <Input placeholder="Buyer Inc." {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="contactPerson"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Contact Person *</FormLabel>
                        <FormControl>
                          <Input placeholder="Jane Doe" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="businessAddress"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Business Address *</FormLabel>
                        <FormControl>
                          <Input placeholder="123 Market St" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="taxId"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Tax ID *</FormLabel>
                        <FormControl>
                          <Input placeholder="12-3456789" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </>
              )}

              {selectedRole === 'supplier' && (
                 <>
                  <FormField
                    control={form.control}
                    name="farmName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Farm/Location Name *</FormLabel>
                        <FormControl>
                          <Input placeholder="Green Acres Farm" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="ownerName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Owner Name *</FormLabel>
                        <FormControl>
                          <Input placeholder="John Farmer" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="produceTypes"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Produce Types *</FormLabel>
                        <FormControl>
                          <Input placeholder="Tomatoes, Corn, etc." {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="physicalAddress"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Physical Address *</FormLabel>
                        <FormControl>
                          <Input placeholder="456 Country Rd" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="certificationDetails"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Certification Details (Optional)</FormLabel>
                        <FormControl>
                          <Input placeholder="Organic Certified" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </>
              )}

               {selectedRole === 'transporter' && (
                 <>
                  <FormField
                    control={form.control}
                    name="companyName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Company Name *</FormLabel>
                        <FormControl>
                          <Input placeholder="Speedy Transport Ltd." {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="contactPerson"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Contact Person *</FormLabel>
                        <FormControl>
                          <Input placeholder="Mike Wheeler" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="officeAddress"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Office Address *</FormLabel>
                        <FormControl>
                          <Input placeholder="789 Logistics Ave" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="fleetSize"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Fleet Size *</FormLabel>
                        <FormControl>
                           <Input type="number" placeholder="10" {...field} onChange={e => field.onChange(e.target.value === '' ? undefined : +e.target.value)} value={field.value ?? ''} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="licenseNumbers"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>License Numbers *</FormLabel>
                        <FormControl>
                          <Input placeholder="DOT: 12345, MC: 67890" {...field} />
                        </FormControl>
                         <FormMessage />
                      </FormItem>
                    )}
                  />
                </>
              )}

               {selectedRole === 'driver' && (
                 <>
                  <FormField
                    control={form.control}
                    name="fullName"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Full Name *</FormLabel>
                        <FormControl>
                          <Input placeholder="Alex Driver" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="homeAddress"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Home Address *</FormLabel>
                        <FormControl>
                          <Input placeholder="1 Driver Ln" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="licenseNumber"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>License Number *</FormLabel>
                        <FormControl>
                          <Input placeholder="D12345678" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={form.control}
                    name="experienceYears"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Experience (Years) *</FormLabel>
                        <FormControl>
                           <Input type="number" placeholder="5" {...field} onChange={e => field.onChange(e.target.value === '' ? undefined : +e.target.value)} value={field.value ?? ''} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="vehicleDetails"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Vehicle Details *</FormLabel>
                        <FormControl>
                          <Input placeholder="2020 Freightliner Cascadia" {...field} />
                        </FormControl>
                         <FormMessage />
                      </FormItem>
                    )}
                  />
                </>
              )}

              <Button
                type="submit"
                className="w-full"
                disabled={form.formState.isSubmitting || !selectedRole}
              >
                {form.formState.isSubmitting ? 'Registering...' : 'Register'}
              </Button>
            </form>
          </Form>
        </CardContent>
        <CardFooter className="flex justify-center">
          <p className="text-sm text-muted-foreground">
            Already have an account?{' '}
            <Button variant="link" className="p-0 h-auto" asChild>
               <Link href="/login" className="text-primary hover:underline">
                Login here
               </Link>
            </Button>
          </p>
        </CardFooter>
      </Card>
    </main>
  );
}
