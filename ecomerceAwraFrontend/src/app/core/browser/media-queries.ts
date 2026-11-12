const matches = (query: string): boolean => window.matchMedia(query).matches;

export const prefersReducedMotion = (): boolean => matches('(prefers-reduced-motion: reduce)');

export const hasFinePointer = (): boolean => matches('(pointer: fine)');
