<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	exclude-result-prefixes="fo">
	<xsl:output method="xml" version="1.0"
		omit-xml-declaration="no" indent="yes" />
	<xsl:param name="versionParam" select="'1.0'" />

	<xsl:attribute-set name="border-adn1">
		<xsl:attribute name="border">solid 0.3mm black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="background-color-adn1">
		<xsl:attribute name="background-color">#F2F6F5</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="border-bg-adn1">
		<xsl:attribute name="border">solid 0.3mm black</xsl:attribute>
		<xsl:attribute name="background-color">#F2F6F5</xsl:attribute>
	</xsl:attribute-set>

	<xsl:template match="avpMdl">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="29.7cm" page-width="21cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
					<fo:region-body region-name="xsl-region-body" />
					<fo:region-after region-name="xsl-region-after" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:static-content flow-name="xsl-region-after">
					<fo:block text-align="center" font-size="10pt">18 rue de la Fosse
						aux loups, 95100 Argenteuil
					</fo:block>
					<fo:block text-align="center" font-size="10pt">Tél : 01 39 98 50
						30 - contact@cedre.info
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">

					<fo:table table-layout="fixed" width="100%">
						<fo:table-column column-width="50%" />
						<fo:table-column column-width="50%" />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block>
										<fo:external-graphic
											src="../logos/logo-cedre-50pct.jpg" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-size="16pt" space-after="5mm">AVIS DE PASSAGE
									</fo:block>
									<fo:block font-size="14pt" space-after="5mm">
										<xsl:value-of select="idavp" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>

					<fo:table table-layout="fixed" width="100%"
						space-before="5mm">
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell padding-top="10pt"
									padding-left="10pt"
									xsl:use-attribute-sets="background-color-adn1" height="3cm">
									<fo:block font-size="14pt" font-weight="bold">
										<xsl:value-of select="nomClient" />
									</fo:block>
									<fo:block font-size="14pt">
										<xsl:value-of select="adresse" />
										&#160;
										<xsl:value-of select="codepostal" />
										&#160;
										<xsl:value-of select="ville" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:table table-layout="fixed" width="100%"
						space-before="5mm">
						<fo:table-column column-width="50%" />
						<fo:table-column column-width="50%" />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="12pt">Opération(s) réalisée(s)
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="right" font-size="12pt">Quantités
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:table table-layout="fixed" width="100%"
						border-collapse="collapse" space-before="3mm">
						<fo:table-column column-width="30%" />
						<fo:table-column column-width="50%" />
						<fo:table-column column-width="10%" />
						<fo:table-column column-width="10%" />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell
									xsl:use-attribute-sets="border-bg-adn1"
									padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
									<fo:block font-size="12pt" text-align="center">Prestation
									</fo:block>
								</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="border-bg-adn1"
									padding-top="2pt" padding-bottom="2pt">
									<fo:block font-size="12pt" text-align="center">Dispositif
									</fo:block>
								</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="border-bg-adn1"
									padding-top="2pt" padding-bottom="2pt">
									<fo:block font-size="12pt" text-align="center">Prév.</fo:block>
								</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="border-bg-adn1"
									padding-top="2pt" padding-bottom="2pt">
									<fo:block font-size="12pt" text-align="center">Réal.</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<xsl:apply-templates select="details" />
						</fo:table-body>
					</fo:table>

					<fo:block font-size="12pt" space-before="5mm">
						Par :
						<xsl:value-of select="agents" />
						&#160;
						- Véhicule :
						<xsl:value-of select="vehicule" />
					</fo:block>
					<fo:table table-layout="fixed" width="100%"
						space-before="5mm">
						<fo:table-column column-width="30%" />
						<fo:table-column column-width="50%" />
						<fo:table-column column-width="10%" />
						<fo:table-column column-width="10%" />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell padding-top="5pt"
									padding-bottom="5pt" padding-right="10pt">
									<fo:block font-size="12pt" text-align="right">Le :</fo:block>
								</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="border-adn1" padding-top="5pt"
									padding-bottom="5pt" padding-left="10pt">
									<fo:block font-size="12pt" text-align="left">
										<xsl:value-of select="dateAvp" />
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding-top="5pt"
									padding-bottom="5pt">
									<fo:block font-size="12pt" text-align="center">à</fo:block>
								</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="border-adn1" padding-top="5pt"
									padding-bottom="5pt">
									<fo:block font-size="12pt" text-align="center">
										<xsl:value-of select="heureAvp" />
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:table table-layout="fixed" width="100%"
						space-before="5mm">
						<fo:table-column column-width="30%" />
						<fo:table-column column-width="70%" />
						<fo:table-body>
							<fo:table-row height="4cm">
								<fo:table-cell padding-top="5pt"
									padding-bottom="5pt" padding-right="10pt">
									<fo:block font-size="12pt" text-align="right">Visa client :
									</fo:block>
								</fo:table-cell>
								<fo:table-cell
									xsl:use-attribute-sets="border-adn1" padding-top="5pt"
									padding-bottom="5pt" padding-left="2pt">
									<fo:block font-size="12pt" text-align="left">
										<xsl:value-of select="infosSignataire" />
									</fo:block>
									<fo:block text-align="center">
									<xsl:if test="signaturePresente='true'">
										<fo:external-graphic>
											<xsl:attribute name="src">
             <xsl:value-of select="signature" />
       </xsl:attribute>
										</fo:external-graphic>
										</xsl:if>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<xsl:apply-templates select="photos" />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="details">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="border-adn1"
				padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
				<fo:block font-size="12pt" text-align="left">
					<xsl:value-of select="operation" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="border-adn1"
				padding-top="2pt" padding-bottom="2pt" padding-left="2pt">
				<fo:block font-size="12pt" text-align="left">
					<xsl:value-of select="dispositif" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="border-adn1"
				padding-top="2pt" padding-bottom="2pt" padding-right="3pt">
				<fo:block font-size="12pt" text-align="right">
					<xsl:value-of select="qteprev" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="border-adn1"
				padding-top="2pt" padding-bottom="2pt" padding-right="3pt">
				<fo:block font-size="12pt" text-align="right">
					<xsl:value-of select="qtereal" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	<xsl:template match="photos">
		<fo:block text-align="center">
			<fo:external-graphic width="17cm" height="24cm" content-height="scale-to-fit" content-width="scale-to-fit">
				<xsl:attribute name="src">
         			<xsl:value-of select="url" />
   				</xsl:attribute>
			</fo:external-graphic>
		</fo:block>
		<fo:block font-size="12pt" text-align="center">
			<xsl:value-of select="dhfmt" />
		</fo:block>
	</xsl:template>
</xsl:stylesheet>