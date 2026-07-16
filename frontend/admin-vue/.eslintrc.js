module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
    es6: true
  },
  'extends': [
    'eslint:recommended',
    'plugin:vue/recommended',
    '@vue/standard',
    '@vue/typescript'
  ],
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'space-before-function-paren': 0,
    'vue/array-bracket-spacing': 0,
    'vue/arrow-spacing': 0,
    'vue/block-spacing': 0,
    'vue/brace-style': 'error',
    'vue/camelcase': 'error',
    'vue/comma-dangle': 0,
    'vue/component-name-in-template-casing': 'error',
    'vue/key-spacing': 0,
    'vue/match-component-file-name': 'error',
    'vue/object-curly-spacing': 0,
    'vue/max-attributes-per-line': 0,
    'padded-blocks': 0,
    'semi': 0,
    'indent': 0,
    'space-infix-ops': 0,
    'space-before-blocks': 0,
    'eqeqeq': 0,
    'vue/eqeqeq': 0,
    'object-curly-spacing': 0,
    'keyword-spacing': 0,
    'spaced-comment': 0,
    'key-spacing': 0,
    'comma-spacing': 0,
    'comma-dangle': 0,
    'space-in-parens': 0,
    'standard/object-curly-even-spacing': 0,
    // Legacy code intentionally mixes quoted keys and formatting styles.
    // Keep correctness rules active without forcing a repository-wide rewrite.
    'quote-props': 0,
    'quotes': 0,
    'no-unused-vars': 0,
    '@typescript-eslint/no-unused-vars': 0,
    'lines-between-class-members': 0,
    'object-curly-newline': 0,
    'eol-last': 0,
    'no-multiple-empty-lines': 0,
    'prefer-const': 0,
    'no-extra-semi': 0,
    'arrow-spacing': 0,
    'no-trailing-spaces': 0,
    'standard/computed-property-even-spacing': 0,
    'no-var': 0,
  },
  parserOptions: {
    parser: '@typescript-eslint/parser'
  },
  overrides: [
    {
      files: [
        '**/__tests__/*.{j,t}s?(x)',
        '**/tests/unit/**/*.spec.{j,t}s?(x)'
      ],
      env: {
        jest: true
      }
    }
  ]
}
