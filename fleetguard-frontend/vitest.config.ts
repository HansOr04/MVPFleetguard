import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  test: {
    globals: true,
    projects: [
      {
        // ── Pruebas UNITARIAS ──────────────────────────────────────────────
        test: {
          name: 'unit',
          include: [
            'src/__tests__/**/*.test.ts',
            'src/__tests__/**/*.test.tsx',
          ],
          environment: 'jsdom',
          setupFiles: ['./vitest.setup.ts'],
          globals: true,
          coverage: {
            provider: 'v8',
            reporter: ['text', 'html', 'lcov'],
            reportsDirectory: './coverage',
            include: [
              'src/validators/**',
              'src/hooks/**',
              'src/lib/api.ts',
              'src/services/**',
            ],
            exclude: [
              'src/**/*.test.ts',
              'src/**/*.test.tsx',
              'src/**/*.integration.test.tsx',
              'src/lib/mocks/**',
              'src/types/**',
            ],
            thresholds: {
              lines: 80,
              functions: 80,
              branches: 70,
              statements: 80,
            },
          },
        },
        plugins: [react()],
        resolve: {
          alias: { '@': path.resolve(__dirname, './src') },
        },
      },
      {
        // ── Pruebas de INTEGRACIÓN ─────────────────────────────────────────
        test: {
          name: 'integration',
          include: ['src/__integration__/**/*.integration.test.tsx'],
          environment: 'jsdom',
          setupFiles: [
            // 1. Variables de entorno (DEBE ir primero, antes de importar api.ts)
            './src/__integration__/setup/env.ts',
            // 2. Matchers de jest-dom
            './vitest.setup.ts',
            // 3. Mocks globales de Next.js
            './src/__integration__/setup/next-mocks.ts',
          ],
          globals: true,
        },
        plugins: [react()],
        resolve: {
          alias: { '@': path.resolve(__dirname, './src') },
        },
      },
    ],
  },
});