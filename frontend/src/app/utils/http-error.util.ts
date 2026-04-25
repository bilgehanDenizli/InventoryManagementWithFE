export function extractApiError(error: unknown, fallback: string): string {
  const candidate = error as { error?: { message?: string } | string };

  if (typeof candidate?.error === 'string' && candidate.error.trim()) {
    return candidate.error;
  }

  if (
    typeof candidate?.error === 'object' &&
    candidate.error !== null &&
    'message' in candidate.error &&
    typeof candidate.error.message === 'string' &&
    candidate.error.message.trim()
  ) {
    return candidate.error.message;
  }

  return fallback;
}
