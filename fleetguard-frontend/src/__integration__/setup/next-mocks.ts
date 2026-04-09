import { vi } from 'vitest';

// Mock de Next.js para todos los tests de integración
vi.mock('next/navigation', () => ({
  usePathname: () => '/',
  useSearchParams: () => ({ get: () => null }),
}));

vi.mock('next/link', () => ({
  default: ({
    href,
    children,
    ...props
  }: {
    href: string;
    children: React.ReactNode;
    [key: string]: unknown;
  }) => {
    const React = require('react');
    return React.createElement('a', { href, ...props }, children);
  },
}));

vi.mock('next/font/google', () => ({
  Inter: () => ({ className: 'inter' }),
}));