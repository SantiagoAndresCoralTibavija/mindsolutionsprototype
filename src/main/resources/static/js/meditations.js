// JavaScript para los reproductores de meditación

// Función principal para inicializar reproductores
function initMeditationPlayers() {

    // La seccion de meditaciones llega por htmx, asi que esta funcion se
    // invoca tambien desde otras pantallas donde los controles no existen.
    // Sin esta guarda, el primer addEventListener sobre null tumba el script.
    if (!document.getElementById('audio1')) return;

    
    // ========== REPRODUCTOR 1 ==========
    const audio1 = document.getElementById('audio1');
    const playPause1 = document.getElementById('playPause1');
    const back10_1 = document.getElementById('back10_1');
    const plus10_1 = document.getElementById('plus10_1');
    const volumen1 = document.getElementById('volumen1');
    const volumenValue1 = document.getElementById('volumenValue1');
    const currentTime1 = document.getElementById('currentTime1');
    const songLength1 = document.getElementById('songLength1');
    const progress1 = document.getElementById('progress1');
    const lluvia1 = document.getElementById('lluvia1');
    const viento1 = document.getElementById('viento1');
    const naturaleza1 = document.getElementById('naturaleza1');
    const stop1 = document.getElementById('stop1');
    const rainsfx1 = document.getElementById('rainsfx1');
    const windsfx1 = document.getElementById('windsfx1');
    const naturesfx1 = document.getElementById('naturesfx1');

    // Función para formatear tiempo
    const formatTime = (secs) => {
        const minutes = Math.floor(secs / 60);
        const seconds = Math.floor(secs % 60);
        return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    };

    // Cargar duración del audio 1
    audio1.addEventListener('loadedmetadata', () => {
        songLength1.textContent = formatTime(audio1.duration);
    });

    // Actualizar progreso del audio 1
    audio1.addEventListener('timeupdate', () => {
        currentTime1.textContent = formatTime(audio1.currentTime);
        const percentage = (audio1.currentTime / audio1.duration) * 100;
        progress1.style.width = percentage + '%';
    });

    // Controles de reproducción 1
    playPause1.addEventListener('click', () => {
        if (audio1.paused) {
            audio1.play();
            playPause1.innerHTML = '<i data-lucide="pause" class="h-6 w-6"></i>';
        } else {
            audio1.pause();
            playPause1.innerHTML = '<i data-lucide="play" class="h-6 w-6"></i>';
        }
        lucide.createIcons();
    });

    back10_1.addEventListener('click', () => {
        audio1.currentTime -= 10;
    });

    plus10_1.addEventListener('click', () => {
        audio1.currentTime += 10;
    });

    // Control de volumen 1
    volumen1.addEventListener('input', (e) => {
        const value = e.target.value;
        audio1.volume = value / 100;
        volumenValue1.textContent = value;
    });

    // Sonidos ambiente 1
    stop1.addEventListener('click', () => {
        rainsfx1.pause();
        windsfx1.pause();
        naturesfx1.pause();
        rainsfx1.currentTime = 0;
        windsfx1.currentTime = 0;
        naturesfx1.currentTime = 0;
    });

    lluvia1.addEventListener('click', () => {
        windsfx1.pause();
        naturesfx1.pause();
        rainsfx1.volume = 0.1; // Volumen reducido al 30%
        rainsfx1.play();
    });

    viento1.addEventListener('click', () => {
        rainsfx1.pause();
        naturesfx1.pause();
        windsfx1.volume = 0.1; // Volumen reducido al 30%
        windsfx1.play();
    });

    naturaleza1.addEventListener('click', () => {
        rainsfx1.pause();
        windsfx1.pause();
        naturesfx1.volume = 0.1; // Volumen reducido al 30%
        naturesfx1.play();
    });

    // ========== REPRODUCTOR 2 ==========
    const audio2 = document.getElementById('audio2');
    const playPause2 = document.getElementById('playPause2');
    const back10_2 = document.getElementById('back10_2');
    const plus10_2 = document.getElementById('plus10_2');
    const volumen2 = document.getElementById('volumen2');
    const volumenValue2 = document.getElementById('volumenValue2');
    const currentTime2 = document.getElementById('currentTime2');
    const songLength2 = document.getElementById('songLength2');
    const progress2 = document.getElementById('progress2');
    const lluvia2 = document.getElementById('lluvia2');
    const viento2 = document.getElementById('viento2');
    const naturaleza2 = document.getElementById('naturaleza2');
    const stop2 = document.getElementById('stop2');
    const rainsfx2 = document.getElementById('rainsfx2');
    const windsfx2 = document.getElementById('windsfx2');
    const naturesfx2 = document.getElementById('naturesfx2');

    // Cargar duración del audio 2
    audio2.addEventListener('loadedmetadata', () => {
        songLength2.textContent = formatTime(audio2.duration);
    });

    // Actualizar progreso del audio 2
    audio2.addEventListener('timeupdate', () => {
        currentTime2.textContent = formatTime(audio2.currentTime);
        const percentage = (audio2.currentTime / audio2.duration) * 100;
        progress2.style.width = percentage + '%';
    });

    // Controles de reproducción 2
    playPause2.addEventListener('click', () => {
        if (audio2.paused) {
            audio2.play();
            playPause2.innerHTML = '<i data-lucide="pause" class="h-6 w-6"></i>';
        } else {
            audio2.pause();
            playPause2.innerHTML = '<i data-lucide="play" class="h-6 w-6"></i>';
        }
        lucide.createIcons();
    });

    back10_2.addEventListener('click', () => {
        audio2.currentTime -= 10;
    });

    plus10_2.addEventListener('click', () => {
        audio2.currentTime += 10;
    });

    // Control de volumen 2
    volumen2.addEventListener('input', (e) => {
        const value = e.target.value;
        audio2.volume = value / 100;
        volumenValue2.textContent = value;
    });

    // Sonidos ambiente 2
    stop2.addEventListener('click', () => {
        rainsfx2.pause();
        windsfx2.pause();
        naturesfx2.pause();
        rainsfx2.currentTime = 0;
        windsfx2.currentTime = 0;
        naturesfx2.currentTime = 0;
    });

    lluvia2.addEventListener('click', () => {
        windsfx2.pause();
        naturesfx2.pause();
        rainsfx2.volume = 0.1; // Volumen reducido al 30%
        rainsfx2.play();
    });

    viento2.addEventListener('click', () => {
        rainsfx2.pause();
        naturesfx2.pause();
        windsfx2.volume = 0.1; // Volumen reducido al 30%
        windsfx2.play();
    });

    naturaleza2.addEventListener('click', () => {
        rainsfx2.pause();
        windsfx2.pause();
        naturesfx2.volume = 0.1; // Volumen reducido al 30%
        naturesfx2.play();
    });
}

// Inicializar al cargar la página
/*
 * Se expone en window porque la seccion de meditaciones no viene en la carga
 * inicial de la pagina: la trae htmx cuando el usuario entra a esa pestania,
 * momento en el que DOMContentLoaded ya ocurrio hace rato. app.js la vuelve a
 * llamar despues de cada swap. Como los nodos se reemplazan enteros, no hay
 * riesgo de acumular listeners.
 */
window.initMeditationPlayers = initMeditationPlayers;

document.addEventListener('DOMContentLoaded', initMeditationPlayers);

// Re-inicializar después de que HTMX cargue el fragmento
document.body.addEventListener('htmx:afterSwap', function(evt) {
    if (evt.detail.target.id === 'main-content') {
        initMeditationPlayers();
    }
});
