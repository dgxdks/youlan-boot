import highlight from 'highlight.js'
import 'highlight.js/scss/github.scss'

export const LANGUAGE_JAVA = 'java'
export const LANGUAGE_XML = 'xml'
export const LANGUAGE_HTML = 'html'
export const LANGUAGE_VUE = 'vue'
export const LANGUAGE_JAVASCRIPT = 'javascript'
export const LANGUAGE_SQL = 'sql'
export const EMPTY_CONTENT = '&nbsp;'

highlight.registerLanguage(LANGUAGE_JAVA, require('highlight.js/lib/languages/java'))
highlight.registerLanguage(LANGUAGE_XML, require('highlight.js/lib/languages/xml'))
highlight.registerLanguage(LANGUAGE_HTML, require('highlight.js/lib/languages/xml'))
highlight.registerLanguage(LANGUAGE_VUE, require('highlight.js/lib/languages/xml'))
highlight.registerLanguage(LANGUAGE_JAVASCRIPT, require('highlight.js/lib/languages/javascript'))
highlight.registerLanguage(LANGUAGE_SQL, require('highlight.js/lib/languages/sql'))
export default {
  highlight(content, language) {
    const result = highlight.highlight(content, { language: language || LANGUAGE_JAVA, ignoreIllegals: true })
    return result.value || EMPTY_CONTENT
  },
  highlightJava(content) {
    return this.highlight(content, LANGUAGE_JAVA)
  },
  highlightXml(content) {
    return this.highlight(content, LANGUAGE_XML)
  },
  highlightHtml(content) {
    return this.highlight(content, LANGUAGE_HTML)
  },
  highlightVue(content) {
    return this.highlight(content, LANGUAGE_VUE)
  },
  highlightJavascript(content) {
    return this.highlight(content, LANGUAGE_JAVASCRIPT)
  },
  highlightSql(content) {
    return this.highlight(content, LANGUAGE_SQL)
  }
}
