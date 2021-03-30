package com.horizen

import scorex.core.settings.ScorexSettings

import scala.concurrent.duration.FiniteDuration


case class WebSocketSettings(address: String,
                             connectionTimeout: FiniteDuration,
                             reconnectionDelay: FiniteDuration,
                             reconnectionMaxAttempts: Int,
                             allowNoConnectionInRegtest: Boolean = true // In Regtest allow to forge new blocks without connection to MC node, for example.
                            )

case class GenesisDataSettings(scGenesisBlockHex: String,
                               scId: String,
                               mcBlockHeight: Int,
                               powData: String,
                               mcNetwork: String,
                               withdrawalEpochLength: Int
                              )

case class withdrawalEpochCertificateSettings(submitterIsEnabled: Boolean,
                                              signersPublicKeys: Seq[String],
                                              signersThreshold: Int,
                                              signersSecrets: Seq[String],
                                              provingKeyFilePath: String,
                                              verificationKeyFilePath: String)

case class ForgerSettings(automaticForging: Boolean = false)

case class WalletSettings(seed: String,
                          genesisSecrets: Seq[String])

case class SidechainSettings(
                              scorexSettings: ScorexSettings,
                              genesisData: GenesisDataSettings,
                              websocket: WebSocketSettings,
                              withdrawalEpochCertificateSettings: withdrawalEpochCertificateSettings,
                              wallet: WalletSettings,
                              forger: ForgerSettings
                            )