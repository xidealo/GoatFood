import { type FormEvent, type ReactNode, useEffect, useRef, useState } from 'react';
import { ArrowDownRight, ArrowRight, Check, ChevronDown, Menu, Plus, X } from 'lucide-react';
import { Route, Switch, useLocation, Router as WouterRouter, Link } from 'wouter';import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ErrorBoundary } from '@/components/error-boundary';
import { Toaster } from '@/components/ui/toaster';
import { TooltipProvider } from '@/components/ui/tooltip';
import NotFound from '@/pages/not-found';
import LegalDocumentPage from '@/pages/legal-document';
import { getLegalDocument } from '@/content/legal';

const queryClient = new QueryClient();

const faqs = [
  ['Что такое GoatFood?', 'GoatFood – платформа для приёма заказов. У кафе своё мобильное приложение и при необходимости сайт. Заказы приходят администратору в отдельное приложение.'],
  ['Как заказывает гость?', 'Через ваше приложение в App Store и Google Play или через сайт. Меню с фото, доставка или самовывоз – как настроите на смене.'],
  ['Как подключиться?', 'Оставьте заявку, и мы созвонимся на 20 минут. После этого соберём первую версию меню и запустим её в течение нескольких рабочих дней.'],
  ['Какая комиссия за заказ?', 'Платформа берёт 3% с заказа. Никаких скрытых платежей, платы за установку или длинных контрактов.'],
  ['В каких регионах вы работаете?', 'Работаем в Тверской, Московской и Ярославской областях — от Твери и Москвы до Ярославля и городов вокруг.'],
  ['Сколько стоит разработать приложение для доставки еды?', 'Запуск и разработка под ваш бренд бесплатны. Вы платите только комиссию 3% с заказа — без абонентской платы и платы за установку.'],
  ['Чем GoatFood отличается от агрегаторов вроде Яндекс Еды?', 'У вас своё приложение и сайт под вашим брендом, без высокой комиссии агрегатора. Гость заказывает напрямую у вас, а не через чужой маркетплейс.'],
  ['Нужно ли маленькому кафе своё приложение?', 'Да, если хотите свою доставку или самовывоз без комиссии агрегатора. Даже небольшому кафе удобно принимать заказы в своём приложении и вести их в CRM администратора.'],
];

const geoRegions = [
  {
    title: 'Тверская область',
    label: 'домашний регион',
    tone: 'lime' as const,
    cities: ['Тверь', 'Ржев', 'Торжок', 'Вышний Волочёк', 'Кимры', 'Конаково', 'Бологое', 'Удомля', 'Осташков', 'Бежецк', 'Кашин', 'Лихославль'],
  },
  {
    title: 'Московская область и Москва',
    label: 'столичный регион',
    tone: 'paper' as const,
    cities: ['Москва', 'Химки', 'Мытищи', 'Балашиха', 'Подольск', 'Красногорск', 'Одинцово', 'Люберцы', 'Королёв', 'Домодедово', 'Щёлково', 'Сергиев Посад', 'Клин', 'Дмитров'],
  },
  {
    title: 'Ярославская область',
    label: 'золотое кольцо',
    tone: 'paper' as const,
    cities: ['Ярославль', 'Рыбинск', 'Переславль-Залесский', 'Тутаев', 'Углич', 'Ростов', 'Гаврилов-Ям', 'Данилов'],
  },
];

function Logo() {
  return <span className="bb-logo" aria-label="GoatFood"><span className="bb-logo-mark">g.</span><span className="bb-logo-word">GoatFood</span></span>;
}

function PhonePreview() {
  return (
    <div className="bb-stage" aria-label="Предпросмотр мобильного меню GoatFood" data-testid="preview-phone">
      <div className="bb-orbit" />
      <img
        className="bb-guest-screen"
        src="/guest-menu.png"
        alt="Меню гостя в мобильном приложении GoatFood"
      />
      <div className="bb-floating-tag"><span>ваш бренд</span>в кармане</div>
    </div>
  );
}

function AdminPreview() {
  return (
    <div className="bb-admin-stage" aria-label="Приложение администратора GoatFood" data-testid="preview-dashboard">
      <img className="bb-admin-screen bb-admin-phone-back" src="/admin-orders.png" alt="Список заказов в приложении администратора" />
      <img className="bb-admin-screen bb-admin-phone-front" src="/admin-order.png" alt="Карточка заказа в приложении администратора" />
      <div className="bb-floating-tag bb-admin-tag"><span>приложение</span>админа</div>
    </div>
  );
}

const LEAD_URL = 'https://api.web3forms.com/submit';
const LEAD_ERROR = 'Не удалось отправить. Попробуйте ещё раз';

function ContactModal({ onClose }: { onClose: () => void }) {
  const [sent, setSent] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const nameRef = useRef<HTMLInputElement>(null);
  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const form = event.currentTarget;
    const accessKey = import.meta.env.VITE_WEB3FORMS_KEY;
    if (!accessKey) {
      setError(LEAD_ERROR);
      return;
    }
    const data = {
      access_key: accessKey,
      subject: 'Заявка GoatFood',
      name: (form.elements.namedItem('name') as HTMLInputElement).value,
      contact: (form.elements.namedItem('contact') as HTMLInputElement).value,
      email: (form.elements.namedItem('email') as HTMLInputElement).value,
      message: (form.elements.namedItem('message') as HTMLTextAreaElement).value,
    };
    setSending(true);
    setError(null);
    try {
      const response = await fetch(LEAD_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      });
      const result = (await response.json().catch(() => null)) as { success?: boolean } | null;
      if (!response.ok || !result?.success) {
        setError(LEAD_ERROR);
        return;
      }
      setSent(true);
    } catch {
      setError(LEAD_ERROR);
    } finally {
      setSending(false);
    }
  };
  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => { if (event.key === 'Escape') onClose(); };
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [onClose]);
  useEffect(() => { nameRef.current?.focus(); }, []);
  return (
    <div className="bb-modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <div className="bb-modal bb-card-dark" role="dialog" aria-modal="true" aria-labelledby="contact-title" aria-describedby={sent ? undefined : 'contact-intro'}>
        <button className="bb-modal-close" onClick={onClose} aria-label="Закрыть форму" data-testid="button-close-contact"><X size={20} /></button>
        {sent ? <div className="bb-success" role="status"><strong>Заявка отправлена</strong><span>Спасибо! Мы напишем вам в течение рабочего дня, чтобы назначить короткий разговор.</span></div> : <>
          <span className="bb-section-label">поговорим о вашем кафе</span>
          <h2 id="contact-title" className="bb-display">Запустим<br /><span className="bb-lime">ваше меню</span></h2>
          <p id="contact-intro">Расскажите о кафе – покажем, как GoatFood будет выглядеть именно для вас.</p>
          <form className="bb-form" onSubmit={submit}>
            <label htmlFor="contact-name">Имя<input ref={nameRef} id="contact-name" className="bb-input" name="name" placeholder="Как к вам обращаться?" required data-testid="input-contact-name" /></label>
            <label htmlFor="contact-contact">Контакт<input id="contact-contact" className="bb-input" name="contact" placeholder="Телефон или Telegram" required data-testid="input-contact-contact" /></label>
            <label htmlFor="contact-email">Почта<input id="contact-email" className="bb-input" name="email" type="email" placeholder="hello@mail.ru" required data-testid="input-contact-email" /></label>
            <label htmlFor="contact-message">О кафе<textarea id="contact-message" className="bb-input" name="message" rows={3} placeholder="Название и город" data-testid="input-contact-message" /></label>
            {error ? <p className="bb-form-error" role="alert">{error}</p> : null}
            <button className="bb-button bb-button-primary" type="submit" disabled={sending} data-testid="button-submit-contact">{sending ? 'Отправляем…' : <>Отправить заявку <ArrowRight size={16} /></>}</button>
          </form>
        </>}
      </div>
    </div>
  );
}

function Home() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [openFaq, setOpenFaq] = useState<number | null>(0);
  const openContact = () => { setModalOpen(true); setMenuOpen(false); };
  useEffect(() => {
    document.body.style.overflow = modalOpen ? 'hidden' : '';
    return () => { document.body.style.overflow = ''; };
  }, [modalOpen]);
  return (
    <main className="bb-page">
      <div className="bb-container">
        <header className="bb-header">
          <a className="bb-logo" href="#top" data-testid="link-home"><Logo /></a>
          <nav className={`bb-nav ${menuOpen ? 'is-open' : ''}`} aria-label="Основная навигация">
            <a href="#product" onClick={() => setMenuOpen(false)} data-testid="link-product">Продукт</a>
            <a href="#conditions" onClick={() => setMenuOpen(false)} data-testid="link-conditions">Условия</a>
            <a href="#geo" onClick={() => setMenuOpen(false)} data-testid="link-geo">География</a>
            <a href="#faq" onClick={() => setMenuOpen(false)} data-testid="link-faq">Вопросы</a>
          </nav>
          <button className="bb-menu" onClick={() => setMenuOpen(!menuOpen)} aria-label="Открыть меню" aria-expanded={menuOpen} data-testid="button-mobile-menu"><Menu size={22} /></button>
          <button className="bb-header-cta" onClick={openContact} data-testid="button-header-contact">Обсудить запуск <ArrowDownRight size={15} /></button>
        </header>
      </div>

      <section className="bb-container bb-hero" id="top">
        <div className="bb-hero-grid">
          <div>
            <div className="bb-eyebrow bb-reveal">своё приложение и сайт доставки еды для кафе</div>
            <h1 className="bb-display bb-reveal bb-delay-1">Кафе,<br />которое <em>заказывают</em><br />с телефона</h1>
            <p className="bb-lede bb-reveal bb-delay-2">GoatFood – мобильное приложение для доставки еды и сайт под брендом вашего кафе. Работаем в Тверской, Московской и Ярославской областях: гость заказывает у вас, администратор ведёт заказ в CRM.</p>
            <div className="bb-hero-actions bb-reveal bb-delay-3"><button className="bb-button bb-button-primary" onClick={openContact} data-testid="button-hero-contact">Запустить GoatFood <ArrowRight size={16} /></button><a className="bb-button bb-button-ghost" href="#product" data-testid="link-hero-product">Посмотреть как это работает <ArrowDownRight size={17} /></a></div>
            <p className="bb-small-note">приложение для гостя ·  CRM приложение для администратора</p>
            <p className="bb-store-links">
              <a href="https://apps.apple.com/ru/developer/mark-shavlovskiy/id1651086345" target="_blank" rel="noreferrer" data-testid="link-app-store">App Store</a>
              <a href="https://play.google.com/store/apps/developer?id=xIdealo&hl=ru" target="_blank" rel="noreferrer" data-testid="link-google-play">Google Play</a>
              <a href="https://play.google.com/store/apps/details?id=com.bunbeauty.fooddeliveryadmin&hl=ru" target="_blank" rel="noreferrer" data-testid="link-admin-app">Приложение админа</a>
            </p>
          </div>
          <AdminPreview />
        </div>
      </section>

      <section className="bb-product bb-section bb-on-lime" id="product">
        <div className="bb-container">
          <div className="bb-product-intro"><div><span className="bb-section-label">01 / продукт</span><h2 className="bb-display bb-section-heading">Не просто меню<br /><em>Новый ритм кафе</em></h2></div><p>Статусы, стоп-лист и время доставки – гость видит это в момент заказа</p></div>
          <div className="bb-feature-layout">
            <div className="bb-feature-list">
              <article className="bb-feature"><span className="bb-feature-number">01</span><h3>Статусы под контролем</h3><p>Администратор ведёт заказ от нового до готов. Гость не звонит «ну что там»</p></article>
              <article className="bb-feature"><span className="bb-feature-number">02</span><h3>Стоп-лист без сюрпризов</h3><p>Скрыли позицию – её нельзя заказать. Никто не купит то, чего уже нет</p></article>
              <article className="bb-feature"><span className="bb-feature-number">03</span><h3>Режим под смену</h3><p>Доставка, самовывоз или оба. Курьеры не вышли – оставляете только самовывоз. Время доставки крутите от загрузки заведения, гость видит его при оформлении заказа</p></article>
            </div>
            <PhonePreview />
          </div>
        </div>
      </section>

      <section className="bb-conditions bb-section bb-on-dark" id="conditions">
        <div className="bb-container">
          <span className="bb-section-label">02 / условия</span>
          <h2 className="bb-display bb-section-heading">Платите только<br />когда <em>зарабатываете</em></h2>
          <div className="bb-condition-grid">
            <div className="bb-commission-card bb-card-lime"><span className="bb-label">КОМИССИЯ С ЗАКАЗА</span><div className="bb-commission"><strong>3</strong><span>%</span></div></div>
            <div className="bb-condition-copy bb-card-dark"><h3>Честная модель<br />для живого бизнеса</h3><p>Никаких абонентских платежей, платы за запуск и обязательств на год. GoatFood растёт вместе с вашим кафе.</p><div className="bb-checks"><span><i /><Check size={13} /> Бесплатный запуск</span><span><i /><Check size={13} /> Поддержка 24/7</span></div></div>
            <div className="bb-band bb-card-paper"><strong>Первый месяц – за наш счёт</strong><span>Проверим всё на реальных заказах</span></div>
          </div>
        </div>
      </section>

      <section className="bb-geo bb-section bb-on-dark" id="geo">
        <div className="bb-container">
          <span className="bb-section-label">03 / география</span>
          <h2 className="bb-display bb-section-heading">Работаем в Тверской, Московской и Ярославской областях</h2>
          <p className="bb-geo-lede">Мобильное приложение для доставки еды и сайт под брендом вашего кафе — в областных центрах и небольших городах. Гости заказывают напрямую у вас, без комиссии агрегатора.</p>
          <div className="bb-geo-grid">
            {geoRegions.map((region) => (
              <article className={`bb-geo-card bb-card-${region.tone}`} key={region.title}>
                <div className="bb-geo-card-top">
                  <span className="bb-label">{region.label}</span>
                  <span className="bb-geo-count">{region.cities.length} городов</span>
                </div>
                <h3>{region.title}</h3>
                <div className="bb-geo-cities">
                  {region.cities.map((city) => (
                    <span className="bb-geo-city" key={city}>{city}</span>
                  ))}
                </div>
              </article>
            ))}
          </div>
          <div className="bb-geo-cta bb-card-paper">
            <div>
              <strong>Вашего города нет в списке?</strong>
              <span>Напишите — подскажем, когда сможем запустить у вас.</span>
            </div>
            <button className="bb-button bb-button-primary" onClick={openContact} data-testid="button-geo-contact">
              Обсудить запуск <ArrowRight size={15} />
            </button>
          </div>
        </div>
      </section>

      <section className="bb-faq-section bb-section bb-on-lime" id="faq">
        <div className="bb-container bb-faq-wrap">
          <div><span className="bb-section-label">04 / вопросы</span><h2 className="bb-display bb-section-heading">Всё важное –<br /><em>здесь</em></h2></div>
          <div><div className="bb-faq-list">{faqs.map(([question, answer], index) => <div className="bb-faq-item" key={question}><button className="bb-faq-question" onClick={() => setOpenFaq(openFaq === index ? null : index)} aria-expanded={openFaq === index} data-testid={`button-faq-${index}`}><span>{question}</span>{openFaq === index ? <Plus size={18} /> : <ChevronDown size={18} />}</button><div className={`bb-faq-answer ${openFaq === index ? 'open' : ''}`}><p>{answer}</p></div></div>)}</div><div className="bb-faq-side bb-card-dark"><span className="bb-section-label">не нашли ответ?</span><p>Расскажем всё про ваш формат, город и меню – без презентаций на 48 слайдов.</p><button className="bb-button bb-button-primary" onClick={openContact} data-testid="button-faq-contact">Задать вопрос <ArrowRight size={15} /></button></div></div>
        </div>
      </section>

      <div className="bb-cta-section bb-on-dark">
        <section className="bb-container bb-cta">
          <span className="bb-section-label">готовы попробовать?</span><h2 className="bb-display">Ваше кафе<br /><span>В его телефоне</span></h2>
          <div className="bb-cta-row"><button className="bb-button bb-button-primary" onClick={openContact} data-testid="button-cta-contact">Поговорить о запуске <ArrowRight size={16} /></button><p>20 минут, чтобы понять, подходит ли вам GoatFood</p></div>
        </section>
        <footer className="bb-container bb-footer">
          <div className="bb-footer-row">
            <a href="#top" data-testid="link-footer-home"><Logo /></a>
            <a href="mailto:shavl.mark@yandex.ru" data-testid="link-email">shavl.mark@yandex.ru</a>
          </div>
          <nav className="bb-footer-links" aria-label="Юридические документы">
            <Link href="/user-agreement" data-testid="link-user-agreement">Пользовательское соглашение</Link>
            <Link href="/privacy-policy" data-testid="link-privacy-policy">Политика конфиденциальности</Link>
            <Link href="/terms-of-service" data-testid="link-terms-of-service">Условия предоставления услуг</Link>
          </nav>
          <p className="bb-footer-legal">© 2026 ИП Шавловский Марк Вячеславович · ОГРНИП 322695200049377 · ИНН 691010434605</p>
        </footer>
      </div>
      {modalOpen && <ContactModal onClose={() => setModalOpen(false)} />}
    </main>
  );
}

function RoutedErrorBoundary({ children }: { children: ReactNode }) {
  const [location] = useLocation();
  return <ErrorBoundary resetKey={location}>{children}</ErrorBoundary>;
}

function LegalRoute({ slug }: { slug: string }) {
  const document = getLegalDocument(slug);
  if (!document) return <NotFound />;
  return <LegalDocumentPage document={document} />;
}

function Router() {
  return (
    <RoutedErrorBoundary>
      <Switch>
        <Route path="/" component={Home} />
        <Route path="/user-agreement">{() => <LegalRoute slug="user-agreement" />}</Route>
        <Route path="/privacy-policy">{() => <LegalRoute slug="privacy-policy" />}</Route>
        <Route path="/terms-of-service">{() => <LegalRoute slug="terms-of-service" />}</Route>
        <Route component={NotFound} />
      </Switch>
    </RoutedErrorBoundary>
  );
}

function App() {
  return <QueryClientProvider client={queryClient}><TooltipProvider><WouterRouter base={import.meta.env.BASE_URL.replace(/\/$/, '')}><Router /></WouterRouter><Toaster /></TooltipProvider></QueryClientProvider>;
}

export default App;