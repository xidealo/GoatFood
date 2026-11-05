import { useEffect } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { AlertCircle } from 'lucide-react';

export default function NotFound() {
  useEffect(() => {
    let robots = document.querySelector('meta[name="robots"]') as HTMLMetaElement | null;
    const previous = robots?.getAttribute('content') ?? null;
    if (!robots) {
      robots = document.createElement('meta');
      robots.setAttribute('name', 'robots');
      document.head.appendChild(robots);
    }
    robots.setAttribute('content', 'noindex, nofollow');
    return () => {
      if (previous === null) {
        robots?.remove();
      } else {
        robots?.setAttribute('content', previous);
      }
    };
  }, []);

  return (
    <div className="min-h-screen w-full flex items-center justify-center bg-gray-50">
      <Card className="w-full max-w-md mx-4">
        <CardContent className="pt-6">
          <div className="flex mb-4 gap-2">
            <AlertCircle className="h-8 w-8 text-red-500" />
            <h1 className="text-2xl font-bold text-gray-900">
              Страница не найдена
            </h1>
          </div>

          <p className="mt-4 text-sm text-gray-600">
            Такой страницы нет. Вернитесь на{' '}
            <a href="/" className="underline">
              главную
            </a>
            .
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
