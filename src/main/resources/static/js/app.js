/*
 * Logica de UI global de MindSolutions.
 *
 * Se carga UNA sola vez desde layout/base.html. Todos los listeners viven aqui
 * y usan delegacion de eventos, para que sigan funcionando sobre contenido que
 * htmx inserte despues sin volver a registrarse.
 *
 * Regla del proyecto: ningun fragmento htmx trae <script> propio.
 */
(function () {
    'use strict';

    // ---------------------------------------------------------------
    // Iconos: lucide reemplaza cada <i data-lucide> por un <svg>, asi que
    // hay que volver a pasarlo cada vez que entra HTML nuevo.
    // ---------------------------------------------------------------
    function renderIcons() {
        if (window.lucide) window.lucide.createIcons();
    }

    // ---------------------------------------------------------------
    // Chat
    // ---------------------------------------------------------------
    function scrollChatToBottom(smooth) {
        var chat = document.getElementById('chat-messages');
        if (!chat) return;
        chat.scrollTo({
            top: chat.scrollHeight,
            behavior: smooth ? 'smooth' : 'auto'
        });
    }

    // ---------------------------------------------------------------
    // Avisos.
    //
    // Se usa un toast en una esquina, no un modal: un modal bloqueante para
    // decir "revise los datos" es desmedido y corta el hilo de lo que la
    // persona estaba haciendo. Los estilos salen de las mismas variables del
    // tema (ver .ms-toast en input.css), no del look por defecto de la libreria.
    // ---------------------------------------------------------------
    window.msToast = function (message, kind) {
        if (!window.Swal) return;

        Swal.fire({
            toast: true,
            position: 'bottom-end',
            icon: kind === 'success' ? 'success' : (kind === 'info' ? 'info' : 'error'),
            title: message,
            showConfirmButton: false,
            timer: kind === 'error' ? 6000 : 4000,
            timerProgressBar: true,
            customClass: {popup: 'ms-toast'}
        });
    };

    // ---------------------------------------------------------------
    // Navegacion: marcar la seccion activa
    // ---------------------------------------------------------------
    function setActiveNav(button) {
        var nav = document.getElementById('main-nav');
        if (!nav || !button) return;

        nav.querySelectorAll('.nav-button').forEach(function (b) {
            b.removeAttribute('data-active');
        });
        button.setAttribute('data-active', 'true');
    }

    // ---------------------------------------------------------------
    // Panel de administracion: pestanias
    // ---------------------------------------------------------------
    function showAdminTab(name) {
        document.querySelectorAll('.admin-content').forEach(function (c) {
            c.classList.add('hidden');
        });
        document.querySelectorAll('.admin-tab').forEach(function (t) {
            t.removeAttribute('data-active');
        });

        var content = document.getElementById('admin-' + name);
        if (content) content.classList.remove('hidden');

        var tab = document.getElementById('tab-' + name);
        if (tab) tab.setAttribute('data-active', 'true');

        renderIcons();
    }

    // ---------------------------------------------------------------
    // Un solo listener de click para toda la aplicacion
    // ---------------------------------------------------------------
    document.addEventListener('click', function (e) {

        var navButton = e.target.closest('#main-nav .nav-button');
        if (navButton) {
            setActiveNav(navButton);
            return;
        }

        var adminTab = e.target.closest('[data-admin-tab]');
        if (adminTab) {
            showAdminTab(adminTab.getAttribute('data-admin-tab'));
            return;
        }

        // Frases de arranque del chat: rellenan el campo, nunca envian.
        // La persona siempre revisa y ajusta antes de mandar algo suyo.
        var starter = e.target.closest('[data-starter]');
        if (starter) {
            var input = document.getElementById('new-chat-input');
            if (input) {
                input.value = starter.getAttribute('data-starter');
                input.focus();
                input.setSelectionRange(input.value.length, input.value.length);
            }
            return;
        }

        var closer = e.target.closest('[data-close-modal]');
        if (closer) {
            var modal = closer.closest('[data-modal]');
            if (modal) modal.remove();
            return;
        }

        // Click en el fondo oscuro del modal
        if (e.target.matches('[data-modal]')) {
            e.target.remove();
        }
    });

    // Escape cierra el modal abierto.
    document.addEventListener('keydown', function (e) {
        if (e.key !== 'Escape') return;
        var modal = document.querySelector('[data-modal]');
        if (modal) modal.remove();
    });

    // ---------------------------------------------------------------
    // Confirmacion de acciones destructivas.
    //
    // Se intercepta en fase de captura para adelantarse a htmx. El formulario
    // se marca como confirmado y se reenvia; sin esa marca entraria en bucle.
    // ---------------------------------------------------------------
    document.addEventListener('submit', function (e) {
        var form = e.target.closest('[data-confirm]');
        if (!form || form.dataset.confirmed === 'true') return;

        e.preventDefault();
        e.stopPropagation();

        if (!window.Swal) {
            form.dataset.confirmed = 'true';
            if (window.htmx) window.htmx.trigger(form, 'submit');
            return;
        }

        Swal.fire({
            title: form.getAttribute('data-confirm-title') || '¿Confirmar?',
            text: form.getAttribute('data-confirm'),
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: form.getAttribute('data-confirm-ok') || 'Continuar',
            cancelButtonText: 'Cancelar',
            reverseButtons: true,
            focusCancel: true,
            customClass: {
                popup: 'ms-dialog',
                confirmButton: 'ms-dialog-confirm',
                cancelButton: 'ms-dialog-cancel'
            },
            buttonsStyling: false
        }).then(function (result) {
            if (!result.isConfirmed) return;
            form.dataset.confirmed = 'true';
            if (window.htmx) {
                window.htmx.trigger(form, 'submit');
            } else {
                form.submit();
            }
            delete form.dataset.confirmed;
        });
    }, true);

    // ---------------------------------------------------------------
    // Ciclo de vida de htmx
    // ---------------------------------------------------------------

    // Antes de reemplazar el contenido central, silenciar cualquier audio de
    // meditaciones: si no, la pista sigue sonando despues de cambiar de seccion.
    document.body.addEventListener('htmx:beforeSwap', function (evt) {
        var target = evt.detail.target;
        if (!target || target.id !== 'main-content') return;

        target.querySelectorAll('audio').forEach(function (a) {
            a.pause();
        });
    });

    document.body.addEventListener('htmx:afterSwap', function (evt) {
        renderIcons();

        var target = evt.detail.target;
        if (!target) return;

        if (target.id === 'chat-messages') {
            scrollChatToBottom(true);
        }

        if (target.id === 'main-content') {
            scrollChatToBottom(false);

            // Los reproductores se inicializan aqui y no en DOMContentLoaded:
            // esta seccion llega por htmx mucho despues de que ese evento paso.
            if (window.initMeditationPlayers) window.initMeditationPlayers();

            // Al cambiar de seccion, el foco vuelve al inicio del contenido
            // para quien navega con teclado o lector de pantalla.
            target.setAttribute('tabindex', '-1');
            target.focus({preventScroll: true});
        }

        // Al recargar la tabla de cuentas, cerrar el modal que la disparo.
        if (target.id === 'users-table-body') {
            document.querySelectorAll('[data-modal]').forEach(function (m) {
                m.remove();
            });
        }
    });

    // Si el servidor responde con un error, decirlo en vez de dejarlo en silencio.
    document.body.addEventListener('htmx:responseError', function () {
        window.msToast('No pudimos completar la accion. Intenta de nuevo.', 'error');
    });

    document.body.addEventListener('htmx:sendError', function () {
        window.msToast('Parece que no hay conexion.', 'error');
    });

    // ---------------------------------------------------------------
    // Arranque
    // ---------------------------------------------------------------
    document.addEventListener('DOMContentLoaded', function () {
        renderIcons();
        scrollChatToBottom(false);

        // Mensaje que dejo el registro antes de redirigir al ingreso.
        var flash = document.getElementById('flash-success');
        if (flash) {
            window.msToast(flash.getAttribute('data-message'), 'success');
        }
    });
})();
