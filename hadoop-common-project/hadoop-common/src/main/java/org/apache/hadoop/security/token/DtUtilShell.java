begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|Text
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
name|security
operator|.
name|UserGroupInformation
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
name|tools
operator|.
name|CommandShell
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
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *  DtUtilShell is a set of command line token file management operations.  */
end_comment

begin_class
DECL|class|DtUtilShell
specifier|public
class|class
name|DtUtilShell
extends|extends
name|CommandShell
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DtUtilShell
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FORMAT_SUBSTRING
specifier|private
specifier|static
specifier|final
name|String
name|FORMAT_SUBSTRING
init|=
literal|"[-format ("
operator|+
name|DtFileOperations
operator|.
name|FORMAT_JAVA
operator|+
literal|"|"
operator|+
name|DtFileOperations
operator|.
name|FORMAT_PB
operator|+
literal|")]"
decl_stmt|;
DECL|field|DT_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|DT_USAGE
init|=
literal|"hadoop dtutil "
operator|+
literal|"[-keytab<keytab_file> -principal<principal_name>] "
operator|+
literal|"subcommand (help|print|get|edit|append|cancel|remove|renew) "
operator|+
name|FORMAT_SUBSTRING
operator|+
literal|" [-alias<alias>] filename..."
decl_stmt|;
comment|// command line options
DECL|field|HELP
specifier|private
specifier|static
specifier|final
name|String
name|HELP
init|=
literal|"help"
decl_stmt|;
DECL|field|KEYTAB
specifier|private
specifier|static
specifier|final
name|String
name|KEYTAB
init|=
literal|"-keytab"
decl_stmt|;
DECL|field|PRINCIPAL
specifier|private
specifier|static
specifier|final
name|String
name|PRINCIPAL
init|=
literal|"-principal"
decl_stmt|;
DECL|field|PRINT
specifier|private
specifier|static
specifier|final
name|String
name|PRINT
init|=
literal|"print"
decl_stmt|;
DECL|field|GET
specifier|private
specifier|static
specifier|final
name|String
name|GET
init|=
literal|"get"
decl_stmt|;
DECL|field|EDIT
specifier|private
specifier|static
specifier|final
name|String
name|EDIT
init|=
literal|"edit"
decl_stmt|;
DECL|field|APPEND
specifier|private
specifier|static
specifier|final
name|String
name|APPEND
init|=
literal|"append"
decl_stmt|;
DECL|field|CANCEL
specifier|private
specifier|static
specifier|final
name|String
name|CANCEL
init|=
literal|"cancel"
decl_stmt|;
DECL|field|REMOVE
specifier|private
specifier|static
specifier|final
name|String
name|REMOVE
init|=
literal|"remove"
decl_stmt|;
DECL|field|RENEW
specifier|private
specifier|static
specifier|final
name|String
name|RENEW
init|=
literal|"renew"
decl_stmt|;
DECL|field|IMPORT
specifier|private
specifier|static
specifier|final
name|String
name|IMPORT
init|=
literal|"import"
decl_stmt|;
DECL|field|RENEWER
specifier|private
specifier|static
specifier|final
name|String
name|RENEWER
init|=
literal|"-renewer"
decl_stmt|;
DECL|field|SERVICE
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE
init|=
literal|"-service"
decl_stmt|;
DECL|field|ALIAS
specifier|private
specifier|static
specifier|final
name|String
name|ALIAS
init|=
literal|"-alias"
decl_stmt|;
DECL|field|FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|FORMAT
init|=
literal|"-format"
decl_stmt|;
comment|// configuration state from args, conf
DECL|field|keytab
specifier|private
name|String
name|keytab
init|=
literal|null
decl_stmt|;
DECL|field|principal
specifier|private
name|String
name|principal
init|=
literal|null
decl_stmt|;
DECL|field|alias
specifier|private
name|Text
name|alias
init|=
literal|null
decl_stmt|;
DECL|field|service
specifier|private
name|Text
name|service
init|=
literal|null
decl_stmt|;
DECL|field|renewer
specifier|private
name|String
name|renewer
init|=
literal|null
decl_stmt|;
DECL|field|format
specifier|private
name|String
name|format
init|=
name|DtFileOperations
operator|.
name|FORMAT_PB
decl_stmt|;
DECL|field|tokenFiles
specifier|private
name|ArrayList
argument_list|<
name|File
argument_list|>
name|tokenFiles
init|=
literal|null
decl_stmt|;
DECL|field|firstFile
specifier|private
name|File
name|firstFile
init|=
literal|null
decl_stmt|;
comment|/**    * Parse arguments looking for Kerberos keytab/principal.    * If both are found: remove both from the argument list and attempt login.    * If only one of the two is found: remove it from argument list, log warning    * and do not attempt login.    * If neither is found: return original args array, doing nothing.    * Return the pruned args array if either flag is present.    */
DECL|method|maybeDoLoginFromKeytabAndPrincipal (String[] args)
specifier|private
name|String
index|[]
name|maybeDoLoginFromKeytabAndPrincipal
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|savedArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|args
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|current
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|equals
argument_list|(
name|PRINCIPAL
argument_list|)
condition|)
block|{
name|principal
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|current
operator|.
name|equals
argument_list|(
name|KEYTAB
argument_list|)
condition|)
block|{
name|keytab
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|savedArgs
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|newSize
init|=
name|savedArgs
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|newSize
operator|!=
name|args
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|principal
operator|!=
literal|null
operator|&&
name|keytab
operator|!=
literal|null
condition|)
block|{
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"-principal and -keytab not both specified!  "
operator|+
literal|"Kerberos login not attempted."
argument_list|)
expr_stmt|;
block|}
return|return
name|savedArgs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|newSize
index|]
argument_list|)
return|;
block|}
return|return
name|args
return|;
block|}
comment|/**    * Parse the command line arguments and initialize subcommand.    * Also will attempt to perform Kerberos login if both -principal and -keytab    * flags are passed in args array.    * @param args    * @return 0 if the argument(s) were recognized, 1 otherwise    * @throws Exception    */
annotation|@
name|Override
DECL|method|init (String[] args)
specifier|protected
name|int
name|init
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
literal|0
operator|==
name|args
operator|.
name|length
condition|)
block|{
return|return
literal|1
return|;
block|}
name|tokenFiles
operator|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|=
name|maybeDoLoginFromKeytabAndPrincipal
argument_list|(
name|args
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|String
name|command
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|HELP
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|PRINT
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Print
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|GET
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Get
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|EDIT
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Edit
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|APPEND
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Append
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|CANCEL
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Remove
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|REMOVE
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Remove
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|RENEW
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Renew
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
name|IMPORT
argument_list|)
condition|)
block|{
name|setSubCommand
argument_list|(
operator|new
name|Import
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|ALIAS
argument_list|)
condition|)
block|{
name|alias
operator|=
operator|new
name|Text
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|SERVICE
argument_list|)
condition|)
block|{
name|service
operator|=
operator|new
name|Text
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|RENEWER
argument_list|)
condition|)
block|{
name|renewer
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|FORMAT
argument_list|)
condition|)
block|{
name|format
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|!
name|format
operator|.
name|equals
argument_list|(
name|DtFileOperations
operator|.
name|FORMAT_JAVA
argument_list|)
operator|&&
operator|!
name|format
operator|.
name|equals
argument_list|(
name|DtFileOperations
operator|.
name|FORMAT_PB
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"-format must be '"
operator|+
name|DtFileOperations
operator|.
name|FORMAT_JAVA
operator|+
literal|"' or '"
operator|+
name|DtFileOperations
operator|.
name|FORMAT_PB
operator|+
literal|"' not '"
operator|+
name|format
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
else|else
block|{
for|for
control|(
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|tokenFiles
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|firstFile
operator|==
literal|null
condition|)
block|{
name|firstFile
operator|=
name|f
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tokenFiles
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|firstFile
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Must provide a filename to all commands."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCommandUsage ()
specifier|public
name|String
name|getCommandUsage
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%n%s%n   %s%n   %s%n   %s%n   %s%n   %s%n   %s%n   %s%n   %s%n%n"
argument_list|,
name|DT_USAGE
argument_list|,
operator|(
operator|new
name|Print
argument_list|()
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|,
operator|(
operator|new
name|Get
argument_list|()
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|,
operator|(
operator|new
name|Edit
argument_list|()
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|,
operator|(
operator|new
name|Append
argument_list|()
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|,
operator|(
operator|new
name|Remove
argument_list|(
literal|true
argument_list|)
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|,
operator|(
operator|new
name|Remove
argument_list|(
literal|false
argument_list|)
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|,
operator|(
operator|new
name|Renew
argument_list|()
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|,
operator|(
operator|new
name|Import
argument_list|()
operator|)
operator|.
name|getUsage
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Print
specifier|private
class|class
name|Print
extends|extends
name|SubCommand
block|{
DECL|field|PRINT_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|PRINT_USAGE
init|=
literal|"dtutil print [-alias<alias>] filename..."
decl_stmt|;
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|File
name|tokenFile
range|:
name|tokenFiles
control|)
block|{
name|DtFileOperations
operator|.
name|printTokenFile
argument_list|(
name|tokenFile
argument_list|,
name|alias
argument_list|,
name|getConf
argument_list|()
argument_list|,
name|getOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|PRINT_USAGE
return|;
block|}
block|}
DECL|class|Get
specifier|private
class|class
name|Get
extends|extends
name|SubCommand
block|{
DECL|field|GET_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|GET_USAGE
init|=
literal|"dtutil get URL "
operator|+
literal|"[-service<scheme>] "
operator|+
name|FORMAT_SUBSTRING
operator|+
literal|"[-alias<alias>] [-renewer<renewer>] filename"
decl_stmt|;
DECL|field|PREFIX_HTTP
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_HTTP
init|=
literal|"http://"
decl_stmt|;
DECL|field|PREFIX_HTTPS
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_HTTPS
init|=
literal|"https://"
decl_stmt|;
DECL|field|url
specifier|private
name|String
name|url
init|=
literal|null
decl_stmt|;
DECL|method|Get ()
specifier|public
name|Get
parameter_list|()
block|{ }
DECL|method|Get (String arg)
specifier|public
name|Get
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|url
operator|=
name|arg
expr_stmt|;
block|}
DECL|method|isGenericUrl ()
specifier|public
name|boolean
name|isGenericUrl
parameter_list|()
block|{
return|return
name|url
operator|.
name|startsWith
argument_list|(
name|PREFIX_HTTP
argument_list|)
operator|||
name|url
operator|.
name|startsWith
argument_list|(
name|PREFIX_HTTPS
argument_list|)
return|;
block|}
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
if|if
condition|(
name|service
operator|!=
literal|null
operator|&&
operator|!
name|isGenericUrl
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Only provide -service with http/https URL."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|service
operator|==
literal|null
operator|&&
name|isGenericUrl
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Must provide -service with http/https URL."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|url
operator|.
name|indexOf
argument_list|(
literal|"://"
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"URL does not contain a service specification: "
operator|+
name|url
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|DtFileOperations
operator|.
name|getTokenFile
argument_list|(
name|firstFile
argument_list|,
name|format
argument_list|,
name|alias
argument_list|,
name|service
argument_list|,
name|url
argument_list|,
name|renewer
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|GET_USAGE
return|;
block|}
block|}
DECL|class|Edit
specifier|private
class|class
name|Edit
extends|extends
name|SubCommand
block|{
DECL|field|EDIT_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|EDIT_USAGE
init|=
literal|"dtutil edit -service<service> -alias<alias> "
operator|+
name|FORMAT_SUBSTRING
operator|+
literal|"filename..."
decl_stmt|;
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"must pass -service field with dtutil edit command"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|alias
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"must pass -alias field with dtutil edit command"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|File
name|tokenFile
range|:
name|tokenFiles
control|)
block|{
name|DtFileOperations
operator|.
name|aliasTokenFile
argument_list|(
name|tokenFile
argument_list|,
name|format
argument_list|,
name|alias
argument_list|,
name|service
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|EDIT_USAGE
return|;
block|}
block|}
DECL|class|Append
specifier|private
class|class
name|Append
extends|extends
name|SubCommand
block|{
DECL|field|APPEND_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|APPEND_USAGE
init|=
literal|"dtutil append "
operator|+
name|FORMAT_SUBSTRING
operator|+
literal|"filename..."
decl_stmt|;
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|DtFileOperations
operator|.
name|appendTokenFiles
argument_list|(
name|tokenFiles
argument_list|,
name|format
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|APPEND_USAGE
return|;
block|}
block|}
DECL|class|Remove
specifier|private
class|class
name|Remove
extends|extends
name|SubCommand
block|{
DECL|field|REMOVE_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|REMOVE_USAGE
init|=
literal|"dtutil remove -alias<alias> "
operator|+
name|FORMAT_SUBSTRING
operator|+
literal|" filename..."
decl_stmt|;
DECL|field|CANCEL_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|CANCEL_USAGE
init|=
literal|"dtutil cancel -alias<alias> "
operator|+
name|FORMAT_SUBSTRING
operator|+
literal|" filename..."
decl_stmt|;
DECL|field|cancel
specifier|private
name|boolean
name|cancel
init|=
literal|false
decl_stmt|;
DECL|method|Remove (boolean arg)
specifier|public
name|Remove
parameter_list|(
name|boolean
name|arg
parameter_list|)
block|{
name|cancel
operator|=
name|arg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
if|if
condition|(
name|alias
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"-alias flag is not optional for remove or cancel"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|File
name|tokenFile
range|:
name|tokenFiles
control|)
block|{
name|DtFileOperations
operator|.
name|removeTokenFromFile
argument_list|(
name|cancel
argument_list|,
name|tokenFile
argument_list|,
name|format
argument_list|,
name|alias
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
if|if
condition|(
name|cancel
condition|)
block|{
return|return
name|CANCEL_USAGE
return|;
block|}
return|return
name|REMOVE_USAGE
return|;
block|}
block|}
DECL|class|Renew
specifier|private
class|class
name|Renew
extends|extends
name|SubCommand
block|{
DECL|field|RENEW_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|RENEW_USAGE
init|=
literal|"dtutil renew -alias<alias> filename..."
decl_stmt|;
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
if|if
condition|(
name|alias
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"-alias flag is not optional for renew"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|File
name|tokenFile
range|:
name|tokenFiles
control|)
block|{
name|DtFileOperations
operator|.
name|renewTokenFile
argument_list|(
name|tokenFile
argument_list|,
name|format
argument_list|,
name|alias
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|RENEW_USAGE
return|;
block|}
block|}
DECL|class|Import
specifier|private
class|class
name|Import
extends|extends
name|SubCommand
block|{
DECL|field|IMPORT_USAGE
specifier|public
specifier|static
specifier|final
name|String
name|IMPORT_USAGE
init|=
literal|"dtutil import<base64> [-alias<alias>] "
operator|+
name|FORMAT_SUBSTRING
operator|+
literal|" filename"
decl_stmt|;
DECL|field|base64
specifier|private
name|String
name|base64
init|=
literal|null
decl_stmt|;
DECL|method|Import ()
name|Import
parameter_list|()
block|{ }
DECL|method|Import (String arg)
name|Import
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|base64
operator|=
name|arg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|DtFileOperations
operator|.
name|importTokenFile
argument_list|(
name|firstFile
argument_list|,
name|format
argument_list|,
name|alias
argument_list|,
name|base64
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUsage ()
specifier|public
name|String
name|getUsage
parameter_list|()
block|{
return|return
name|IMPORT_USAGE
return|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|exit
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|DtUtilShell
argument_list|()
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

