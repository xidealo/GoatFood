import { useEffect } from 'react';
import { Link } from 'wouter';
import { ArrowLeft } from 'lucide-react';
import { type LegalDocument } from '@/content/legal';

type LegalDocumentPageProps = {
  document: LegalDocument;
};

export default function LegalDocumentPage({ document }: LegalDocumentPageProps) {
  useEffect(() => {
    const previousTitle = window.document.title;
    window.document.title = `${document.title} | GoatFood`;

    let descriptionMeta = window.document.querySelector('meta[name="description"]') as HTMLMetaElement | null;
    const previousDescription = descriptionMeta?.getAttribute('content') ?? null;
    if (!descriptionMeta) {
      descriptionMeta = window.document.createElement('meta');
      descriptionMeta.setAttribute('name', 'description');
      window.document.head.appendChild(descriptionMeta);
    }
    descriptionMeta.setAttribute('content', document.description);

    return () => {
      window.document.title = previousTitle;
      if (previousDescription === null) {
        descriptionMeta?.remove();
      } else {
        descriptionMeta?.setAttribute('content', previousDescription);
      }
    };
  }, [document]);

  return (
    <main className="bb-page bb-legal-page bb-on-dark">
      <div className="bb-container bb-legal">
        <header className="bb-legal-header">
          <Link href="/" className="bb-legal-back" data-testid="link-legal-home">
            <ArrowLeft size={16} />
            На главную
          </Link>
          <a className="bb-logo" href="/" data-testid="link-legal-logo">
            <span className="bb-logo-mark">g.</span>
            <span className="bb-logo-word">GoatFood</span>
          </a>
        </header>
        <h1 className="bb-legal-title">{document.title}</h1>
        <div className="bb-legal-body" data-testid="legal-document-body">
          {document.body}
        </div>
      </div>
    </main>
  );
}
