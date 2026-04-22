<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="userSkillLevel" select="'Intermediate'"/>

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <div class="xsl-recipes-container">
            <xsl:apply-templates select="recipes/recipe"/>
        </div>
    </xsl:template>

    <xsl:template match="recipe">
        <xsl:variable name="matchesSkill">
            <xsl:choose>
                <xsl:when test="primaryDifficulty = $userSkillLevel">yes</xsl:when>
                <xsl:otherwise>no</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="cardClass">
            <xsl:choose>
                <xsl:when test="$matchesSkill = 'yes'">recipe-card skill-match</xsl:when>
                <xsl:otherwise>recipe-card skill-other</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <div class="{$cardClass}">
            <div class="recipe-card-header">
                <h3 class="recipe-title"><xsl:value-of select="title"/></h3>
                <xsl:if test="$matchesSkill = 'yes'">
                    <span class="badge badge-match">Matches Your Level</span>
                </xsl:if>
            </div>
            <div class="recipe-card-body">
                <p class="recipe-description"><xsl:value-of select="description"/></p>
                <div class="recipe-meta">
                    <span class="meta-item">
                        <strong>Cuisines:</strong>
                        <xsl:for-each select="cuisines/cuisine">
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">, </xsl:if>
                        </xsl:for-each>
                    </span>
                    <span class="meta-item">
                        <strong>Difficulty:</strong>
                        <xsl:value-of select="primaryDifficulty"/>
                    </span>
                    <span class="meta-item">
                        <strong>Prep:</strong> <xsl:value-of select="prepTime"/> min
                    </span>
                    <span class="meta-item">
                        <strong>Cook:</strong> <xsl:value-of select="cookTime"/> min
                    </span>
                </div>

                <div class="recipe-actions">
                    <a href="/recipes/{@id}" class="btn-details">View Details</a>
                </div>
            </div>
        </div>
    </xsl:template>

</xsl:stylesheet>
