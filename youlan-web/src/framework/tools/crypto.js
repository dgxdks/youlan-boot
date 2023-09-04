import CryptoJs from 'crypto-js'
import JSEncrypt from 'jsencrypt/bin/jsencrypt.min'
import { StrUtil } from '@/framework/tools/index'
import md5 from 'js-md5'

const defaultAesKey = '85SylH6RnDL9H9a/yoqXew=='
const defaultRsaPublicKey = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSSMuUuRP5iY6TkansOjP2sdhgeF7DWjydlhq0uQxbwFrel/HqL3Y8xlKj6/T75WibklM6HiFr094Os+wdudi3GWV/dNgskBPONXiVEOOJWjgPSndtw7UBhmzNI+rUG9dJ18RT18OSl7AuoXXXKpzOL4cQ389Yf0DDpfKGNE0t7wIDAQAB'
export default {
  aesEncrypt(plainText, key) {
    key = StrUtil.isNotBlank(key) ? key : defaultAesKey
    const aesKey = this.isBase64Str(key) ? CryptoJs.enc.Base64.parse(key) : CryptoJs.enc.Utf8.parse(key)
    const content = CryptoJs.enc.Utf8.parse(plainText)
    const encryptText = CryptoJs.AES.encrypt(content, aesKey, {
      mode: CryptoJs.mode.ECB,
      padding: CryptoJs.pad.Pkcs7
    })
    return encryptText.toString()
  },
  aesDecrypt(encryptText, key) {
    key = StrUtil.isNotBlank(key) ? key : defaultAesKey
    const aesKey = CryptoJs.enc.Utf8.parse(key)
    const decryptText = CryptoJs.AES.decrypt(encryptText, aesKey, {
      mode: CryptoJs.mode.ECB,
      padding: CryptoJs.pad.Pkcs7
    })
    return CryptoJs.enc.Utf8.stringify(decryptText).toString()
  },
  rsaEncrypt(plainText, publicKey) {
    publicKey = StrUtil.isNotBlank(publicKey) ? publicKey : defaultRsaPublicKey
    const encryptor = new JSEncrypt()
    encryptor.setPublicKey(publicKey)
    return encryptor.encrypt(plainText)
  },
  rsaDecrypt(encryptText, publicKey) {
    publicKey = StrUtil.isNotBlank(publicKey) ? publicKey : defaultRsaPublicKey
    const encryptor = new JSEncrypt()
    encryptor.setPrivateKey(publicKey)
    return encryptor.decrypt(encryptText)
  },
  base64Encode(str) {
    if (StrUtil.isBlank(str)) {
      return str
    }
    return CryptoJs.enc.Base64.stringify(str)
  },
  base64Decode(str) {
    if (StrUtil.isBlank(str)) {
      return str
    }
    return CryptoJs.enc.Base64.parse(str)
  },
  isBase64Str(str) {
    if (StrUtil.isBlank(str)) {
      return false
    }
    try {
      return btoa(atob(str)) === str
    } catch (error) {
      return false
    }
  },
  md5Encrypt(str) {
    if (StrUtil.isBlank(str)) {
      return str
    }
    return md5(str)
  }
}
