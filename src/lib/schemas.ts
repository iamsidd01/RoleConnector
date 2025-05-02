import { z } from 'zod';

export const roles = ['buyer', 'supplier', 'transporter', 'driver'] as const;
export type Role = (typeof roles)[number];

const phoneRegex = /^\+?[1-9]\d{1,14}$/; // Basic E.164 format check

// Base schema common to all roles during registration
const baseRegistrationSchema = z.object({
  email: z.string().email({ message: 'Invalid email address.' }),
  phone: z.string().regex(phoneRegex, { message: 'Invalid phone number format.' }),
  password: z
    .string()
    .min(8, { message: 'Password must be at least 8 characters long.' }),
  confirmPassword: z.string(),
  role: z.enum(roles, { required_error: 'Please select a role.' }),
});

// Role-specific schemas extending the base
export const buyerSchema = baseRegistrationSchema.extend({
  role: z.literal('buyer'),
  companyName: z.string().min(1, { message: 'Company name is required.' }),
  contactPerson: z.string().min(1, { message: 'Contact person is required.' }),
  businessAddress: z.string().min(1, { message: 'Business address is required.' }),
  taxId: z.string().min(1, { message: 'Tax ID is required.' }),
});

export const supplierSchema = baseRegistrationSchema.extend({
  role: z.literal('supplier'),
  farmName: z.string().min(1, { message: 'Farm/location name is required.' }),
  ownerName: z.string().min(1, { message: 'Owner name is required.' }),
  produceTypes: z.string().min(1, { message: 'Produce types are required.' }),
  physicalAddress: z.string().min(1, { message: 'Physical address is required.' }),
  certificationDetails: z.string().optional(),
});

export const transporterSchema = baseRegistrationSchema.extend({
  role: z.literal('transporter'),
  companyName: z.string().min(1, { message: 'Company name is required.' }),
  contactPerson: z.string().min(1, { message: 'Contact person is required.' }),
  officeAddress: z.string().min(1, { message: 'Office address is required.' }),
  fleetSize: z.coerce.number().int().positive({ message: 'Fleet size must be a positive number.' }),
  licenseNumbers: z.string().min(1, { message: 'License numbers are required.' }),
});

export const driverSchema = baseRegistrationSchema.extend({
  role: z.literal('driver'),
  fullName: z.string().min(1, { message: 'Full name is required.' }),
  homeAddress: z.string().min(1, { message: 'Home address is required.' }),
  licenseNumber: z.string().min(1, { message: 'License number is required.' }),
  experienceYears: z.coerce.number().int().nonnegative({ message: 'Experience must be 0 or more years.' }),
  vehicleDetails: z.string().min(1, { message: 'Vehicle details are required.' }),
});

// Combined registration schema using discriminated union
export const registrationSchema = z
  .discriminatedUnion('role', [
    buyerSchema,
    supplierSchema,
    transporterSchema,
    driverSchema,
  ])
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'], // Set the error path to confirmPassword
  });

export type RegistrationInput = z.infer<typeof registrationSchema>;


// Login schema
export const loginSchema = z.object({
  email: z.string().email({ message: 'Invalid email address.' }),
  password: z.string().min(1, { message: 'Password is required.' }),
});

export type LoginInput = z.infer<typeof loginSchema>;
