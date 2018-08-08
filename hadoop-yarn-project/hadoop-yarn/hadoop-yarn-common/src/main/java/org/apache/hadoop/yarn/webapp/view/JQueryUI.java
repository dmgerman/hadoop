begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp.view
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|text
operator|.
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|djoin
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|join
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|split
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|hamlet2
operator|.
name|HamletSpec
operator|.
name|HTML
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|class|JQueryUI
specifier|public
class|class
name|JQueryUI
extends|extends
name|HtmlBlock
block|{
comment|// UI params
DECL|field|ACCORDION
specifier|public
specifier|static
specifier|final
name|String
name|ACCORDION
init|=
literal|"ui.accordion"
decl_stmt|;
DECL|field|ACCORDION_ID
specifier|public
specifier|static
specifier|final
name|String
name|ACCORDION_ID
init|=
name|ACCORDION
operator|+
literal|".id"
decl_stmt|;
DECL|field|DATATABLES
specifier|public
specifier|static
specifier|final
name|String
name|DATATABLES
init|=
literal|"ui.dataTables"
decl_stmt|;
DECL|field|DATATABLES_ID
specifier|public
specifier|static
specifier|final
name|String
name|DATATABLES_ID
init|=
name|DATATABLES
operator|+
literal|".id"
decl_stmt|;
DECL|field|DATATABLES_SELECTOR
specifier|public
specifier|static
specifier|final
name|String
name|DATATABLES_SELECTOR
init|=
name|DATATABLES
operator|+
literal|".selector"
decl_stmt|;
DECL|field|DIALOG
specifier|public
specifier|static
specifier|final
name|String
name|DIALOG
init|=
literal|"ui.dialog"
decl_stmt|;
DECL|field|DIALOG_ID
specifier|public
specifier|static
specifier|final
name|String
name|DIALOG_ID
init|=
name|DIALOG
operator|+
literal|".id"
decl_stmt|;
DECL|field|DIALOG_SELECTOR
specifier|public
specifier|static
specifier|final
name|String
name|DIALOG_SELECTOR
init|=
name|DIALOG
operator|+
literal|".selector"
decl_stmt|;
DECL|field|PROGRESSBAR
specifier|public
specifier|static
specifier|final
name|String
name|PROGRESSBAR
init|=
literal|"ui.progressbar"
decl_stmt|;
DECL|field|PROGRESSBAR_ID
specifier|public
specifier|static
specifier|final
name|String
name|PROGRESSBAR_ID
init|=
name|PROGRESSBAR
operator|+
literal|".id"
decl_stmt|;
comment|// common CSS classes
DECL|field|_PROGRESSBAR
specifier|public
specifier|static
specifier|final
name|String
name|_PROGRESSBAR
init|=
literal|".ui-progressbar.ui-widget.ui-widget-content.ui-corner-all"
decl_stmt|;
DECL|field|C_PROGRESSBAR
specifier|public
specifier|static
specifier|final
name|String
name|C_PROGRESSBAR
init|=
name|_PROGRESSBAR
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|' '
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
DECL|field|_PROGRESSBAR_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|_PROGRESSBAR_VALUE
init|=
literal|".ui-progressbar-value.ui-widget-header.ui-corner-left"
decl_stmt|;
DECL|field|C_PROGRESSBAR_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|C_PROGRESSBAR_VALUE
init|=
name|_PROGRESSBAR_VALUE
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|' '
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
DECL|field|_INFO_WRAP
specifier|public
specifier|static
specifier|final
name|String
name|_INFO_WRAP
init|=
literal|".info-wrap.ui-widget-content.ui-corner-bottom"
decl_stmt|;
DECL|field|_TH
specifier|public
specifier|static
specifier|final
name|String
name|_TH
init|=
literal|".ui-state-default"
decl_stmt|;
DECL|field|C_TH
specifier|public
specifier|static
specifier|final
name|String
name|C_TH
init|=
name|_TH
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|' '
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
DECL|field|C_TABLE
specifier|public
specifier|static
specifier|final
name|String
name|C_TABLE
init|=
literal|"table"
decl_stmt|;
DECL|field|_INFO
specifier|public
specifier|static
specifier|final
name|String
name|_INFO
init|=
literal|".info"
decl_stmt|;
DECL|field|_ODD
specifier|public
specifier|static
specifier|final
name|String
name|_ODD
init|=
literal|".odd"
decl_stmt|;
DECL|field|_EVEN
specifier|public
specifier|static
specifier|final
name|String
name|_EVEN
init|=
literal|".even"
decl_stmt|;
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|html
operator|.
name|link
argument_list|(
name|root_url
argument_list|(
literal|"static/jquery/themes-1.9.1/base/jquery-ui.css"
argument_list|)
argument_list|)
operator|.
name|link
argument_list|(
name|root_url
argument_list|(
literal|"static/dt-1.9.4/css/jui-dt.css"
argument_list|)
argument_list|)
operator|.
name|script
argument_list|(
name|root_url
argument_list|(
literal|"static/jquery/jquery-3.3.1.min.js"
argument_list|)
argument_list|)
operator|.
name|script
argument_list|(
name|root_url
argument_list|(
literal|"static/jquery/jquery-ui-1.12.1.custom.min.js"
argument_list|)
argument_list|)
operator|.
name|script
argument_list|(
name|root_url
argument_list|(
literal|"static/dt-1.9.4/js/jquery.dataTables.min.js"
argument_list|)
argument_list|)
operator|.
name|script
argument_list|(
name|root_url
argument_list|(
literal|"static/yarn.dt.plugins.js"
argument_list|)
argument_list|)
operator|.
name|script
argument_list|(
name|root_url
argument_list|(
literal|"static/dt-sorting/natural.js"
argument_list|)
argument_list|)
operator|.
name|style
argument_list|(
literal|"#jsnotice { padding: 0.2em; text-align: center; }"
argument_list|,
literal|".ui-progressbar { height: 1em; min-width: 5em }"
argument_list|)
expr_stmt|;
comment|// required
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|initAccordions
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|initDataTables
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|initDialogs
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|initProgressBars
argument_list|(
name|list
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"$(function() {"
argument_list|)
operator|.
name|__
argument_list|(
name|list
operator|.
name|toArray
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"});"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|jsnotice (HTML html)
specifier|public
specifier|static
name|void
name|jsnotice
parameter_list|(
name|HTML
name|html
parameter_list|)
block|{
name|html
operator|.
name|div
argument_list|(
literal|"#jsnotice.ui-state-error"
argument_list|)
operator|.
name|__
argument_list|(
literal|"This page will not function without javascript enabled."
operator|+
literal|" Please enable javascript on your browser."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|html
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"$('#jsnotice').hide();"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
DECL|method|initAccordions (List<String> list)
specifier|protected
name|void
name|initAccordions
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|)
block|{
for|for
control|(
name|String
name|id
range|:
name|split
argument_list|(
name|$
argument_list|(
name|ACCORDION_ID
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|Html
operator|.
name|isValidId
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|String
name|init
init|=
name|$
argument_list|(
name|initID
argument_list|(
name|ACCORDION
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|init
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|init
operator|=
literal|"{autoHeight: false}"
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|"  $('#"
argument_list|,
name|id
argument_list|,
literal|"').accordion("
argument_list|,
name|init
argument_list|,
literal|");"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|initDataTables (List<String> list)
specifier|protected
name|void
name|initDataTables
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|)
block|{
name|String
name|defaultInit
init|=
literal|"{bJQueryUI: true, sPaginationType: 'full_numbers'}"
decl_stmt|;
name|String
name|stateSaveInit
init|=
literal|"bStateSave : true, "
operator|+
literal|"\"fnStateSave\": function (oSettings, oData) { "
operator|+
literal|" data = oData.aoSearchCols;"
operator|+
literal|"for(i =0 ; i< data.length; i ++) {"
operator|+
literal|"data[i].sSearch = \"\""
operator|+
literal|"}"
operator|+
literal|" sessionStorage.setItem( oSettings.sTableId, JSON.stringify(oData) ); }, "
operator|+
literal|"\"fnStateLoad\": function (oSettings) { "
operator|+
literal|"return JSON.parse( sessionStorage.getItem(oSettings.sTableId) );}, "
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|split
argument_list|(
name|$
argument_list|(
name|DATATABLES_ID
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|Html
operator|.
name|isValidId
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|String
name|init
init|=
name|$
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|init
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|init
operator|=
name|defaultInit
expr_stmt|;
block|}
comment|// for inserting stateSaveInit
name|int
name|pos
init|=
name|init
operator|.
name|indexOf
argument_list|(
literal|'{'
argument_list|)
operator|+
literal|1
decl_stmt|;
name|init
operator|=
operator|new
name|StringBuffer
argument_list|(
name|init
argument_list|)
operator|.
name|insert
argument_list|(
name|pos
argument_list|,
name|stateSaveInit
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|join
argument_list|(
name|id
argument_list|,
literal|"DataTable =  $('#"
argument_list|,
name|id
argument_list|,
literal|"').dataTable("
argument_list|,
name|init
argument_list|,
literal|").fnSetFilteringDelay(188);"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|postInit
init|=
name|$
argument_list|(
name|postInitID
argument_list|(
name|DATATABLES
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|postInit
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|postInit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|selector
init|=
name|$
argument_list|(
name|DATATABLES_SELECTOR
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|selector
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|init
init|=
name|$
argument_list|(
name|initSelector
argument_list|(
name|DATATABLES
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|init
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|init
operator|=
name|defaultInit
expr_stmt|;
block|}
name|int
name|pos
init|=
name|init
operator|.
name|indexOf
argument_list|(
literal|'{'
argument_list|)
operator|+
literal|1
decl_stmt|;
name|init
operator|=
operator|new
name|StringBuffer
argument_list|(
name|init
argument_list|)
operator|.
name|insert
argument_list|(
name|pos
argument_list|,
name|stateSaveInit
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|"  $('"
argument_list|,
name|escapeEcmaScript
argument_list|(
name|selector
argument_list|)
argument_list|,
literal|"').dataTable("
argument_list|,
name|init
argument_list|,
literal|").fnSetFilteringDelay(288);"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initDialogs (List<String> list)
specifier|protected
name|void
name|initDialogs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|)
block|{
name|String
name|defaultInit
init|=
literal|"{autoOpen: false, show: transfer, hide: explode}"
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|split
argument_list|(
name|$
argument_list|(
name|DIALOG_ID
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|Html
operator|.
name|isValidId
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|String
name|init
init|=
name|$
argument_list|(
name|initID
argument_list|(
name|DIALOG
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|init
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|init
operator|=
name|defaultInit
expr_stmt|;
block|}
name|String
name|opener
init|=
name|$
argument_list|(
name|djoin
argument_list|(
name|DIALOG
argument_list|,
name|id
argument_list|,
literal|"opener"
argument_list|)
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|"  $('#"
argument_list|,
name|id
argument_list|,
literal|"').dialog("
argument_list|,
name|init
argument_list|,
literal|");"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|opener
operator|.
name|isEmpty
argument_list|()
operator|&&
name|Html
operator|.
name|isValidId
argument_list|(
name|opener
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|"  $('#"
argument_list|,
name|opener
argument_list|,
literal|"').click(function() { "
argument_list|,
literal|"$('#"
argument_list|,
name|id
argument_list|,
literal|"').dialog('open'); return false; });"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|selector
init|=
name|$
argument_list|(
name|DIALOG_SELECTOR
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|selector
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|init
init|=
name|$
argument_list|(
name|initSelector
argument_list|(
name|DIALOG
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|init
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|init
operator|=
name|defaultInit
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|"  $('"
argument_list|,
name|escapeEcmaScript
argument_list|(
name|selector
argument_list|)
argument_list|,
literal|"').click(function() { $(this).children('.dialog').dialog("
argument_list|,
name|init
argument_list|,
literal|"); return false; });"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initProgressBars (List<String> list)
specifier|protected
name|void
name|initProgressBars
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|)
block|{
for|for
control|(
name|String
name|id
range|:
name|split
argument_list|(
name|$
argument_list|(
name|PROGRESSBAR_ID
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|Html
operator|.
name|isValidId
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|String
name|init
init|=
name|$
argument_list|(
name|initID
argument_list|(
name|PROGRESSBAR
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|join
argument_list|(
literal|"  $('#"
argument_list|,
name|id
argument_list|,
literal|"').progressbar("
argument_list|,
name|init
argument_list|,
literal|");"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|initID (String name, String id)
specifier|public
specifier|static
name|String
name|initID
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|djoin
argument_list|(
name|name
argument_list|,
name|id
argument_list|,
literal|"init"
argument_list|)
return|;
block|}
DECL|method|postInitID (String name, String id)
specifier|public
specifier|static
name|String
name|postInitID
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|djoin
argument_list|(
name|name
argument_list|,
name|id
argument_list|,
literal|"postinit"
argument_list|)
return|;
block|}
DECL|method|initSelector (String name)
specifier|public
specifier|static
name|String
name|initSelector
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|djoin
argument_list|(
name|name
argument_list|,
literal|"selector.init"
argument_list|)
return|;
block|}
DECL|method|tableInit ()
specifier|public
specifier|static
name|StringBuilder
name|tableInit
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|(
literal|"{bJQueryUI:true, "
argument_list|)
operator|.
name|append
argument_list|(
literal|"sPaginationType: 'full_numbers', iDisplayLength:20, "
argument_list|)
operator|.
name|append
argument_list|(
literal|"aLengthMenu:[20, 40, 60, 80, 100]"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

