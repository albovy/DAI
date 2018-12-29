<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:l="http://www.esei.uvigo.es/dai/hybridserver">
    <xsl:output method="html" indent="yes" encoding="utf-8" />
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE HTML&gt;</xsl:text>
        <html>
            <head>
                <title>Configuration</title>
            </head>
            <body>
                <div>
                    <h3>Connections</h3>
                    <div id="connections">
                        <xsl:apply-templates select="l:configuration/l:connections" />
                    </div>

                    <h3>Database</h3>
                    <div id="database">
                        <xsl:apply-templates select="l:configuration/l:database" />
                    </div>

                    <h3>Servers</h3>
                    <div id="servers">
                        <xsl:apply-templates select="l:configuration/l:servers" />
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="l:connections">
        <div>
            <h3>
                HTTP<xsl:value-of select="l:http" />
                WebService:<xsl:value-of select="l:webservice" />
                Numero de conexiones:<xsl:value-of select="l:numClients" />
            </h3>
        </div>
    </xsl:template>
    <xsl:template match="l:database">
        <div>
            <h3>
                Usuario:<xsl:value-of select="l:user" />
                Contraseña:<xsl:value-of select="l:password" />
                URL:<xsl:value-of select="l:url" />
            </h3>
        </div>
    </xsl:template>
    <xsl:template match="l:servers">
        <xsl:for-each select="l:server">
            <h4>
                Server:
                <xsl:value-of select="@name" />
            </h4>
            <h6>
                WSDL:
                <xsl:value-of select="@wsdl" />
            </h6>
            <h6>
                NameSpace:
                <xsl:value-of select="@namespace" />
            </h6>
            <h6>
                Service:
                <xsl:value-of select="@service" />
            </h6>
            <h6>
                httpAddress:
                <xsl:value-of select="@httpAddress" />
            </h6>

        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>