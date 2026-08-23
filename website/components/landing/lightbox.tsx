'use client';
import { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

export function Lightbox({
  src,
  label,
  closeLabel,
  onClose,
}: {
  src: string;
  label: string;
  closeLabel: string;
  onClose: () => void;
}) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    document.addEventListener('keydown', onKey);
    const previous = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', onKey);
      document.body.style.overflow = previous;
    };
  }, [onClose]);

  if (typeof document === 'undefined') return null;

  return createPortal(
    <div
      role="dialog"
      aria-modal="true"
      aria-label={label}
      onClick={onClose}
      className="fixed inset-0 z-[100] flex items-center justify-center bg-black/75 p-4 backdrop-blur-sm"
    >
      <button
        type="button"
        onClick={onClose}
        aria-label={closeLabel}
        className="absolute right-4 top-4 flex size-10 items-center justify-center rounded-full bg-white/15 text-white outline-none transition-colors hover:bg-white/25 focus-visible:ring-2 focus-visible:ring-white"
      >
        <X className="size-5" />
      </button>

      <video
        src={src}
        autoPlay
        loop
        controls
        muted
        playsInline
        onClick={(e) => e.stopPropagation()}
        className="max-h-[88vh] w-auto rounded-[1.6rem] shadow-2xl"
      />
    </div>,
    document.body,
  );
}
