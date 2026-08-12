/** @type {import('tailwindcss').Config} */

module.exports = {
    content: [
        "./src/main/resources/templates/**/*.html",
        "./src/main/resources/static/js/**/*.js",
    ],
    theme: {
        extend: {
            colors: {
                paper: {
                    DEFAULT: 'var(--paper)',
                    raised: 'var(--paper-raised)',
                    sunken: 'var(--paper-sunken)',
                },
                ink: {
                    DEFAULT: 'var(--ink)',
                    soft: 'var(--ink-soft)',
                    faint: 'var(--ink-faint)',
                },
                line: {
                    DEFAULT: 'var(--line)',
                    strong: 'var(--line-strong)',
                },
                clay: {
                    DEFAULT: 'var(--clay)',
                    hover: 'var(--clay-hover)',
                    tint: 'var(--clay-tint)',
                    line: 'var(--clay-line)',
                    on: 'var(--on-clay)',
                },
                sage: {
                    DEFAULT: 'var(--sage)',
                    tint: 'var(--sage-tint)',
                    line: 'var(--sage-line)',
                },
                danger: {
                    DEFAULT: 'var(--danger)',
                    tint: 'var(--danger-tint)',
                },
            },

            fontFamily: {
                display: ['Petrona', 'ui-serif', 'Georgia', 'serif'],
                sans: ['"Alegreya Sans"', 'ui-sans-serif', 'system-ui', 'sans-serif'],
            },


            fontSize: {
                'xs': ['0.75rem', {lineHeight: '1.45'}],
                'sm': ['0.875rem', {lineHeight: '1.5'}],
                'base': ['1rem', {lineHeight: '1.6'}],
                'chat': ['1.0625rem', {lineHeight: '1.62'}],
                'lg': ['1.25rem', {lineHeight: '1.35'}],
                'xl': ['1.5625rem', {lineHeight: '1.25'}],
                '2xl': ['1.953rem', {lineHeight: '1.18'}],
                '3xl': ['2.441rem', {lineHeight: '1.12'}],
            },

            borderRadius: {
                'sheet': '1rem',
            },

            transitionTimingFunction: {
                'out-quart': 'cubic-bezier(0.22, 1, 0.36, 1)',
            },
        },
    },
    plugins: [],
}
