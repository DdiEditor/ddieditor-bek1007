<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:sa="http://www.sa.dk/xmlns/diark/1.0">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="sa:archiveIndex">
		<html>
			<head>
				<style type="text/css"> table { border-width: 0px; border-spacing: 0px;
					border-style: none; border-color: gray; border-collapse: separate;
					background-color: white; } table th { border-width: 0px; padding: 1px;
					border-style: inset; border-color: gray; background-color: white;
					-moz-border-radius: ; } table td { border-width: 0px; padding: 1px;
					border-style: inset; border-color: gray; background-color: white;
					-moz-border-radius: ; } </style>
				<title>
					<xsl:value-of select="sa:archiveInformationPackageID"/>
				</title>
			</head>
			<body>
				<strong>
					<xsl:value-of select="sa:archiveInformationPackageID"/>
				</strong>

				<!-- name -->
				<h1>
					<xsl:value-of select="sa:systemName"/>
				</h1>

				<xsl:if test="(sa:alternativeName= '')">
					<h2>Alternative navne</h2>
				</xsl:if>
				<xsl:for-each select="sa:alternativeName">
					<xsl:value-of select="."/>
					<xsl:text>, </xsl:text>
				</xsl:for-each>

				<!-- creator -->
				<h2>Arkivskabere</h2>
				<xsl:for-each select="sa:archiveCreatorList/sa:creatorName">
					<h3>
						<xsl:value-of select="."/>
					</h3>
					<p>
						<strong>Periode start: </strong>
						<xsl:value-of select="//sa:creationPeriodStart"/>
					</p>
					<p>
						<strong>Periode slut: </strong>
						<xsl:value-of select="//sa:creationPeriodEnd"/>
					</p>
				</xsl:for-each>

				<!-- abstract / purpose -->
				<h2>Beskrivelse</h2>

				<h3>Formål</h3>
				<xsl:value-of select="sa:systemPurpose"/>
				<h3>Abstract</h3>
				<xsl:value-of select="sa:systemContent"/>

				<h3>FORM Klassifikation</h3>
				<p>
					<xsl:for-each select="sa:form">
						<p>
							<strong>Version: </strong>
							<xsl:value-of select="sa:formVersion"/>
						</p>
						<p>

							<strong>Klassifikation: </strong>
							<xsl:value-of select="sa:classList/sa:formClass"/>
						</p>
						<p>
							<strong>Tekst: </strong>
							<xsl:value-of select="sa:classList/sa:formClassText"/>
						</p>
					</xsl:for-each>
				</p>

				<!-- content -->
				<h2>Data</h2>
				<strong>Indeholder persondata: </strong>
				<xsl:call-template name="yesno">
					<xsl:with-param name="condition">
						<xsl:value-of select="sa:personalDataRestrictedInfo"/>
					</xsl:with-param>
				</xsl:call-template>			
				<h3>Identifikation af særligt indhold</h3>
				<p>
					<strong>Regions nummer: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:regionNum"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>Kommune nummer: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:komNum"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>CPR nummer: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:cprNum"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>CVR nummer: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:cvrNum"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>Matrikel nummer: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:matrikNum"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>BBR nummer: </strong>					
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:bbrNum"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>WHO sygdomskoder: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:whoSygKod"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>

				<h3>System specifikt</h3>
				<p>
					<strong>Eksistens af sagsbegreb i IT-systemet: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:systemFileConcept"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>SOA arkitektur: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:multipleDataCollection"/>
						</xsl:with-param>
					</xsl:call-template>			
				</p>
				<p>
					<strong>Data kilder: </strong>
					<xsl:choose>
						<xsl:when test="sa:sourceName">
							<xsl:for-each select="sa:sourceName">
								<p>
									<strong>Systemnavn: </strong>
									<xsl:value-of select="."/>
								</p>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>Ikke oplyst</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</p>
				<p>
					<strong>Data brugere: </strong>
					<xsl:choose>
						<xsl:when test="sa:userName">
							<xsl:for-each select="sa:userName">
								<p>
									<strong>Systemnavn: </strong>
									<xsl:value-of select="."/>
								</p>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>Ikke oplyst</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</p>
				<p>
					<strong>Forgænger systemer: </strong>
					<xsl:choose>
						<xsl:when test="sa:predecessorName">
							<xsl:for-each select="sa:predecessorName">
								<tr>
									<td>System</td>
									<td>
										<xsl:value-of select="."/>
									</td>
								</tr>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>Ikke oplyst</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</p>

				<!-- archive specific -->
				<h2>Arkivinformation</h2>
				<h3>Periode</h3>
				<p>
					<strong>Start: </strong>
					<xsl:value-of select="sa:archivePeriodStart"/>
				</p>
				<p>
					<strong>Slut: </strong>
					<xsl:value-of select="sa:archivePeriodEnd"/>
				</p>
				<h3>Afleveringstype</h3>
				<p>
					<strong>Slutaflevering: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:archiveInformationPacketType"/>
						</xsl:with-param>
					</xsl:call-template>					
				</p>
				<p>
					<strong>Periodiskaflevering: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:archiveType"/>
						</xsl:with-param>
					</xsl:call-template>					
				</p>
				<p>
					<strong>ID på tidlige aflevering: </strong>
					<xsl:call-template name="na">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:archiveInformationPackageIDPrevious"/>
						</xsl:with-param>
					</xsl:call-template>
				</p>

				<!-- access -->
				<h2>Adgang</h2>
				<p>
					<strong>Adgangsrestriktioner: </strong>
					<xsl:choose>
						<xsl:when test="sa:archiveRestrictions">
							<xsl:call-template name="yesno">
								<xsl:with-param name="condition">
									<xsl:value-of select="sa:archiveRestrictions"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>Ikke oplyst</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</p>
				<p>
					<strong>Specielle adgangsrestriktioner: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:otherAccessTypeRestrictions"/>
						</xsl:with-param>
					</xsl:call-template>
				</p>
				<p>
					<strong>Godkendelsesarkiv: </strong>
					<xsl:value-of select="sa:archiveApproval"/>
				</p>

				<!-- other documentation -->
				<h2>Anden dokumentation</h2>
				<strong>Indeholder: </strong>
				<xsl:call-template name="yesno">
					<xsl:with-param name="condition">
						<xsl:value-of select="sa:containsDigitalDocuments"/>
					</xsl:with-param>
				</xsl:call-template>
				<p>
					<strong>Søgemiddel til andre sager eller dokumenter: </strong>
					<xsl:call-template name="yesno">
						<xsl:with-param name="condition">
							<xsl:value-of select="sa:searchRelatedOtherRecords"/>
						</xsl:with-param>
					</xsl:call-template>
				</p>
				<xsl:for-each select="sa:relatedRecordsName">
					<p>
						<strong>Dokument/ sag: </strong>
						<xsl:value-of select="."/>
					</p>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="yesno">
		<xsl:param name="condition"/>
		<xsl:choose>
			<xsl:when test="$condition='true'">
				<xsl:text>Ja</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Nej</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="na">
		<xsl:param name="condition"/>
		<xsl:choose>
			<xsl:when test="$condition!=''">
				<xsl:value-of select="$condition"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Ikke oplyst</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
</xsl:stylesheet>
