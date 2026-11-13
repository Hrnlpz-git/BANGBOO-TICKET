
const toggle = document.querySelector(".menu-toggle");
const nav = document.querySelector(".nav");

// Menú para dispositivos móviles
toggle?.addEventListener("click", () => {
  const open = nav.classList.toggle("open");
  toggle.setAttribute("aria-expanded", String(open));
  toggle.textContent = open ? "×" : "☰";
});

nav?.querySelectorAll("a").forEach(link => {
  link.addEventListener("click", () => {
    nav.classList.remove("open");
    toggle?.setAttribute("aria-expanded", "false");

    if (toggle) {
      toggle.textContent = "☰";
    }
  });
});

// Visor de imágenes ampliadas
const lightbox = document.querySelector("#image-lightbox");
const lightboxImage = document.querySelector(".lightbox-image");
const closeButton = document.querySelector(".lightbox-close");

// Captura las imágenes de la galería y las nuevas imágenes
const zoomableImages = document.querySelectorAll(
  ".gallery-image, .zoomable-image"
);

let previousFocus = null;

function openLightbox(image) {
  if (!lightbox || !lightboxImage) return;

  previousFocus = document.activeElement;

  lightboxImage.src = image.src;
  lightboxImage.alt = image.alt;

  lightbox.classList.add("active");
  lightbox.setAttribute("aria-hidden", "false");

  document.body.classList.add("lightbox-open");

  closeButton?.focus();
}

function closeLightbox() {
  if (!lightbox || !lightboxImage) return;

  lightbox.classList.remove("active");
  lightbox.setAttribute("aria-hidden", "true");

  document.body.classList.remove("lightbox-open");

  lightboxImage.src = "";
  lightboxImage.alt = "";

  previousFocus?.focus();
}

// Activar ampliación con clic o teclado
zoomableImages.forEach(image => {
  image.addEventListener("click", () => {
    openLightbox(image);
  });

  image.addEventListener("keydown", event => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      openLightbox(image);
    }
  });
});

// Botón para cerrar
closeButton?.addEventListener("click", closeLightbox);

// Cerrar al hacer clic fuera de la imagen
lightbox?.addEventListener("click", event => {
  if (event.target === lightbox) {
    closeLightbox();
  }
});

// Cerrar con Escape
document.addEventListener("keydown", event => {
  if (
    event.key === "Escape" &&
    lightbox?.classList.contains("active")
  ) {
    closeLightbox();
  }
});
