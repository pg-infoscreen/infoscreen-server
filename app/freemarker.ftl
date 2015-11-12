<#-- Testfunction -->
<#function avg x y>
    <#return (x + y) / 2>
</#function>
<#-- making JSON for freemarker from content -->
<#assign content = content?eval>
