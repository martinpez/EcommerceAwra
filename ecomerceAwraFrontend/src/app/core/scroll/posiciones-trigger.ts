import { ScrollTrigger } from 'gsap/ScrollTrigger';

/**
 * `setPositions` existe en ScrollTrigger pero no esta en los tipos de
 * TypeScript, asi que aqui se declara la parte que hace falta.
 */
type TriggerConPosiciones = ScrollTrigger & {
  setPositions(nuevoInicio: number, nuevoFin: number): void;
};

/**
 * Deja un trigger donde realmente esta el elemento.
 *
 * ## El problema
 *
 * La Home tiene secciones fijadas (el hero y las ediciones). ScrollTrigger
 * mide las posiciones de lo que va DESPUÉS de un pin con el pin deshecho, y
 * luego lo vuelve a aplicar. Durante esa medicion, lo que va despues de un
 * pin esta 900 px mas arriba de lo que estara: el trigger se queda con una
 * posicion equivocada y las animaciones se disparan demasiado pronto, con la
 * seccion todavia fuera de pantalla.
 *
 * ## La solucion
 *
 * `setPositions` dentro de `onRefresh`: el callback se ejecuta al final del
 * refresh, cuando los pines ya estan aplicados, asi que la posicion real que
 * se mide es la buena.
 *
 * @param trigger El trigger a corregir.
 * @param contenedor El elemento que marca el inicio y el final.
 */
export function corregirPosicionesDeSeccion(trigger: ScrollTrigger, contenedor: HTMLElement): void {
  const top = contenedor.getBoundingClientRect().top + window.scrollY;
  (trigger as TriggerConPosiciones).setPositions(
    top - window.innerHeight * 0.88,
    top - window.innerHeight * 0.45,
  );
}
