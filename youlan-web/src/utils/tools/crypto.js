import CryptoJs from 'crypto-js'
import JSEncrypt from 'jsencrypt/bin/jsencrypt.min'
import { StrUtil } from '@/utils/tools/index'

const defaultAesKey = ''
const defaultRsaPublicKey = ''
export default {
  aesEncrypt(plainText, key) {
    key = StrUtil.isNotBlank(key) ? key : defaultAesKey
    const aesKey = CryptoJs.enc.Utf8.parse(key)
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
  }
}
