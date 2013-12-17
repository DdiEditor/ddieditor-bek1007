<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:sa="http://www.sa.dk/xmlns/diark/1.0">
	<xsl:output method="html" indent="yes"/>

	<!-- params -->
	<xsl:param name="avId">SA-Identifikation</xsl:param>
	<xsl:param name="lang">da</xsl:param>
	<xsl:param name="baselocation">/</xsl:param>
	

	<xsl:variable name="labels" select="document('labels.xml')/Labels/Label"/>
	<xsl:template match="sa:contextDocumentationIndex">
		<head>
			<style type="text/css"> table { border-width: 0px; border-spacing: 0px; border-style:
				none; border-color: gray; border-collapse: separate; background-color: white; }
				table th { border-width: 0px; padding: 1px; border-style: inset; border-color: gray;
				background-color: white; -moz-border-radius: ; } table td { border-width: 0px;
				padding: 1px; border-style: inset; border-color: gray; background-color: white;
				-moz-border-radius: ; } </style>
			<title>
				<xsl:value-of select="$avId"/>
			</title>
		</head>
		<html>
			<body>
				<strong>
					<xsl:value-of select="$avId"/>
				</strong>
				<h1>Dokumenter</h1>
				<xsl:for-each select="sa:document">
					<xsl:variable name="docId" select="sa:documentID"/>
					<h2>
						<xsl:value-of select="$docId"/>
						<xsl:text>: </xsl:text>
						<xsl:value-of select="sa:documentTitle"/>
					</h2>
					<button onclick="window.location.href='{$baselocation}/ContextDocumentation/docCollection1/{$docId}'">Hent dokument</button>
					<xsl:if test="sa:documentDescription">
						<p>
							<strong>Beskrivelse: </strong>
							<xsl:value-of select="sa:documentDescription"/>
						</p>
					</xsl:if>
					<xsl:if test="sa:documentAuthor">
						<p>
							<strong>Forfatter: </strong>
							<xsl:for-each select="sa:documentAuthor">
								<xsl:if test="sa:authorName">
									<xsl:value-of select="sa:authorName"/>
								</xsl:if>
								<xsl:if test="sa:authorInstitution">
									<xsl:if test="sa:authorName">
										<xsl:text> - </xsl:text>
									</xsl:if>
									<xsl:value-of select="sa:authorInstitution"/>
								</xsl:if>
								<xsl:text>, </xsl:text>
							</xsl:for-each>
						</p>
					</xsl:if>
					<xsl:if test="sa:documentDate">
						<p>
							<strong>Udgivet: </strong>
							<xsl:value-of select="sa:documentDate"/>
						</p>
					</xsl:if>
					<h3>Kategorier</h3>

					<!-- system information -->
					<xsl:if
						test="sa:documentCategory/sa:systemInformation/sa:systemInformationOther='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemPublication='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemAgencyQualityControl='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemDataTransfer='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemContent='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemPurpose='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemRegulations='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemAdministrativeFunctions='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemPresentationStructure='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemDataProvision='true' or 
							sa:documentCategory/sa:systemInformation/sa:systemPreviousSubsequentFunctions='true'">
						<p>
							<strong>System information: </strong>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemInformationOther='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemInformationOther'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemPublication='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemPublication'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemAgencyQualityControl='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemAgencyQualityControl'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemDataTransfer='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemDataTransfer'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemContent='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemContent'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemPurpose='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemPurpose'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemRegulations='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemRegulations'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemAdministrativeFunctions='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemAdministrativeFunctions'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemPresentationStructure='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemPresentationStructure'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemDataProvision='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemDataProvision'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:systemInformation/sa:systemPreviousSubsequentFunctions='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'systemPreviousSubsequentFunctions'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</p>
					</xsl:if>

					<!-- operational -->
					<xsl:if
						test="sa:documentCategory/sa:operationalInformation/sa:operationalSystemInformation='true' or 
						sa:documentCategory/sa:operationalInformation/sa:operationalSystemConvertedInformation='true' or 
						sa:documentCategory/sa:operationalInformation/sa:operationalSystemSOA='true' or 
						sa:documentCategory/sa:operationalInformation/sa:operationalSystemInformationOther='true'
						">
						<p>
							<strong>Teknisk udformning, drift og udvikling : </strong>
							<xsl:if
								test="sa:documentCategory/sa:operationalInformation/sa:operationalSystemInformation='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'operationalSystemInformation'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:operationalInformation/sa:operationalSystemConvertedInformation='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of
											select="'operationalSystemConvertedInformation'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:operationalInformation/sa:operationalSystemSOA='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'operationalSystemSOA'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:operationalInformation/sa:operationalSystemInformationOther='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'operationalSystemInformationOther'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</p>
					</xsl:if>

					<!-- submission -->
					<xsl:if
						test="sa:documentCategory/sa:submissionInformation/sa:archivalProvisions='true' or 
						sa:documentCategory/sa:submissionInformation/sa:archivalTransformationInformation='true' or 
						sa:documentCategory/sa:submissionInformation/sa:archivalInformationOther='true'">
						<p>
							<strong>Aflevering: </strong>
							<xsl:if
								test="sa:documentCategory/sa:submissionInformation/sa:archivalProvisions='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivalProvisions'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:submissionInformation/sa:archivalTransformationInformation='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivalTransformationInformation'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:submissionInformation/sa:archivalInformationOther='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivalInformationOther'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</p>
					</xsl:if>

					<!-- ingest -->
					<xsl:if
						test="sa:documentCategory/sa:ingestInformation/sa:archivistNotes='true' or 
						sa:documentCategory/sa:ingestInformation/sa:archivalTestNotes='true' or 
						sa:documentCategory/sa:ingestInformation/sa:archivalInformationOther='true'">
						<p>
							<strong>Ingest: </strong>
							<xsl:if
								test="sa:documentCategory/sa:ingestInformation/sa:archivistNotes='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivistNotes'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:ingestInformation/sa:archivalTestNotes='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivalTestNotes'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:ingestInformation/sa:archivalInformationOther='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivalInformationOther'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</p>
					</xsl:if>

					<!-- preservation -->
					<xsl:if
						test="sa:documentCategory/sa:archivalPreservationInformation/sa:archivalMigrationInformation='true' or 
						sa:documentCategory/sa:archivalPreservationInformation/sa:archivalInformationOther='true'">
						<p>
							<strong>Bevaring: </strong>
							<xsl:if
								test="sa:documentCategory/sa:archivalPreservationInformation/sa:archivalMigrationInformation='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivalMigrationInformation'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
							<xsl:if
								test="sa:documentCategory/sa:archivalPreservationInformation/sa:archivalInformationOther='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'archivalInformationOther'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</p>
					</xsl:if>

					<!-- other -->
					<xsl:if
						test="sa:documentCategory/sa:informationOther/sa:informationOther='true'">
						<p>
							<strong>Andet: </strong>
							<xsl:if
								test="sa:documentCategory/sa:informationOther/sa:informationOther='true'">
								<xsl:call-template name="writeOutCategory">
									<xsl:with-param name="name">
										<xsl:value-of select="'informationOther'"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</p>
					</xsl:if>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="writeOutCategory">
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$labels[@id=$name]">
				<xsl:value-of select="$labels[@id=$name]/LabelText[@xml:lang=$lang]/text()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$name"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>, </xsl:text>
	</xsl:template>
</xsl:stylesheet>
