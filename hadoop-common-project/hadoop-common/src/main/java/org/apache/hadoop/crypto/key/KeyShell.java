begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
package|;
end_package

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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|InvalidParameterException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|conf
operator|.
name|Configured
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
operator|.
name|Metadata
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
operator|.
name|Options
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
name|Tool
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

begin_comment
comment|/**  * This program is the CLI utility for the KeyProvider facilities in Hadoop.  */
end_comment

begin_class
DECL|class|KeyShell
specifier|public
class|class
name|KeyShell
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|USAGE_PREFIX
specifier|final
specifier|static
specifier|private
name|String
name|USAGE_PREFIX
init|=
literal|"Usage: hadoop key "
operator|+
literal|"[generic options]\n"
decl_stmt|;
DECL|field|COMMANDS
specifier|final
specifier|static
specifier|private
name|String
name|COMMANDS
init|=
literal|"   [-help]\n"
operator|+
literal|"   ["
operator|+
name|CreateCommand
operator|.
name|USAGE
operator|+
literal|"]\n"
operator|+
literal|"   ["
operator|+
name|RollCommand
operator|.
name|USAGE
operator|+
literal|"]\n"
operator|+
literal|"   ["
operator|+
name|DeleteCommand
operator|.
name|USAGE
operator|+
literal|"]\n"
operator|+
literal|"   ["
operator|+
name|ListCommand
operator|.
name|USAGE
operator|+
literal|"]\n"
decl_stmt|;
DECL|field|LIST_METADATA
specifier|private
specifier|static
specifier|final
name|String
name|LIST_METADATA
init|=
literal|"keyShell.list.metadata"
decl_stmt|;
DECL|field|interactive
specifier|private
name|boolean
name|interactive
init|=
literal|false
decl_stmt|;
DECL|field|command
specifier|private
name|Command
name|command
init|=
literal|null
decl_stmt|;
comment|/** allows stdout to be captured if necessary */
DECL|field|out
specifier|public
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
comment|/** allows stderr to be captured if necessary */
DECL|field|err
specifier|public
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
DECL|field|userSuppliedProvider
specifier|private
name|boolean
name|userSuppliedProvider
init|=
literal|false
decl_stmt|;
comment|/**    * Primary entry point for the KeyShell; called via main().    *    * @param args Command line arguments.    * @return 0 on success and 1 on failure.  This value is passed back to    * the unix shell, so we must follow shell return code conventions:    * the return code is an unsigned character, and 0 means success, and    * small positive integers mean failure.    * @throws Exception    */
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|exitCode
init|=
literal|0
decl_stmt|;
try|try
block|{
name|exitCode
operator|=
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|exitCode
operator|!=
literal|0
condition|)
block|{
return|return
name|exitCode
return|;
block|}
if|if
condition|(
name|command
operator|.
name|validate
argument_list|()
condition|)
block|{
name|command
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|exitCode
operator|=
literal|1
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
return|return
name|exitCode
return|;
block|}
comment|/**    * Parse the command line arguments and initialize the data    *<pre>    * % hadoop key create keyName [-size size] [-cipher algorithm]    *    [-provider providerPath]    * % hadoop key roll keyName [-provider providerPath]    * % hadoop key list [-provider providerPath]    * % hadoop key delete keyName [-provider providerPath] [-i]    *</pre>    * @param args Command line arguments.    * @return 0 on success, 1 on failure.    * @throws IOException    */
DECL|method|init (String[] args)
specifier|private
name|int
name|init
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Options
name|options
init|=
name|KeyProvider
operator|.
name|options
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
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
comment|// parse command line
name|boolean
name|moreTokens
init|=
operator|(
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"create"
argument_list|)
condition|)
block|{
name|String
name|keyName
init|=
literal|"-help"
decl_stmt|;
if|if
condition|(
name|moreTokens
condition|)
block|{
name|keyName
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
name|command
operator|=
operator|new
name|CreateCommand
argument_list|(
name|keyName
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"-help"
operator|.
name|equals
argument_list|(
name|keyName
argument_list|)
condition|)
block|{
name|printKeyShellUsage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
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
literal|"delete"
argument_list|)
condition|)
block|{
name|String
name|keyName
init|=
literal|"-help"
decl_stmt|;
if|if
condition|(
name|moreTokens
condition|)
block|{
name|keyName
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
name|command
operator|=
operator|new
name|DeleteCommand
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"-help"
operator|.
name|equals
argument_list|(
name|keyName
argument_list|)
condition|)
block|{
name|printKeyShellUsage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
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
literal|"roll"
argument_list|)
condition|)
block|{
name|String
name|keyName
init|=
literal|"-help"
decl_stmt|;
if|if
condition|(
name|moreTokens
condition|)
block|{
name|keyName
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
name|command
operator|=
operator|new
name|RollCommand
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"-help"
operator|.
name|equals
argument_list|(
name|keyName
argument_list|)
condition|)
block|{
name|printKeyShellUsage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"list"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|command
operator|=
operator|new
name|ListCommand
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-size"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|&&
name|moreTokens
condition|)
block|{
name|options
operator|.
name|setBitLength
argument_list|(
name|Integer
operator|.
name|parseInt
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
literal|"-cipher"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|&&
name|moreTokens
condition|)
block|{
name|options
operator|.
name|setCipher
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
literal|"-description"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|&&
name|moreTokens
condition|)
block|{
name|options
operator|.
name|setDescription
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
literal|"-attr"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|&&
name|moreTokens
condition|)
block|{
specifier|final
name|String
name|attrval
index|[]
init|=
name|args
index|[
operator|++
name|i
index|]
operator|.
name|split
argument_list|(
literal|"="
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|attr
init|=
name|attrval
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
specifier|final
name|String
name|val
init|=
name|attrval
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|isEmpty
argument_list|()
operator|||
name|val
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"\nAttributes must be in attribute=value form, "
operator|+
literal|"or quoted\nlike \"attribute = value\"\n"
argument_list|)
expr_stmt|;
name|printKeyShellUsage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
name|attr
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"\nEach attribute must correspond to only one value:\n"
operator|+
literal|"atttribute \""
operator|+
name|attr
operator|+
literal|"\" was repeated\n"
argument_list|)
expr_stmt|;
name|printKeyShellUsage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
name|attributes
operator|.
name|put
argument_list|(
name|attr
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-provider"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|&&
name|moreTokens
condition|)
block|{
name|userSuppliedProvider
operator|=
literal|true
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
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
literal|"-metadata"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|LIST_METADATA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-i"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|||
operator|(
literal|"-interactive"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|)
condition|)
block|{
name|interactive
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-help"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|printKeyShellUsage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
else|else
block|{
name|printKeyShellUsage
argument_list|()
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|printKeyShellUsage
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|attributes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|options
operator|.
name|setAttributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|printKeyShellUsage ()
specifier|private
name|void
name|printKeyShellUsage
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|(
name|USAGE_PREFIX
operator|+
name|COMMANDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|command
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"========================================================="
operator|+
literal|"======"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|CreateCommand
operator|.
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|CreateCommand
operator|.
name|DESC
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"========================================================="
operator|+
literal|"======"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|RollCommand
operator|.
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|RollCommand
operator|.
name|DESC
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"========================================================="
operator|+
literal|"======"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|DeleteCommand
operator|.
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|DeleteCommand
operator|.
name|DESC
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"========================================================="
operator|+
literal|"======"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|ListCommand
operator|.
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|ListCommand
operator|.
name|DESC
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Command
specifier|private
specifier|abstract
class|class
name|Command
block|{
DECL|field|provider
specifier|protected
name|KeyProvider
name|provider
init|=
literal|null
decl_stmt|;
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
DECL|method|getKeyProvider ()
specifier|protected
name|KeyProvider
name|getKeyProvider
parameter_list|()
block|{
name|KeyProvider
name|provider
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|KeyProvider
argument_list|>
name|providers
decl_stmt|;
try|try
block|{
name|providers
operator|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|userSuppliedProvider
condition|)
block|{
name|provider
operator|=
name|providers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|KeyProvider
name|p
range|:
name|providers
control|)
block|{
if|if
condition|(
operator|!
name|p
operator|.
name|isTransient
argument_list|()
condition|)
block|{
name|provider
operator|=
name|p
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
return|return
name|provider
return|;
block|}
DECL|method|printProviderWritten ()
specifier|protected
name|void
name|printProviderWritten
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|(
name|provider
operator|+
literal|" has been updated."
argument_list|)
expr_stmt|;
block|}
DECL|method|warnIfTransientProvider ()
specifier|protected
name|void
name|warnIfTransientProvider
parameter_list|()
block|{
if|if
condition|(
name|provider
operator|.
name|isTransient
argument_list|()
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: you are modifying a transient provider."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|execute ()
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|getUsage ()
specifier|public
specifier|abstract
name|String
name|getUsage
parameter_list|()
function_decl|;
block|}
DECL|class|ListCommand
specifier|private
class|class
name|ListCommand
extends|extends
name|Command
block|{
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"list [-provider<provider>] [-metadata] [-help]"
decl_stmt|;
DECL|field|DESC
specifier|public
specifier|static
specifier|final
name|String
name|DESC
init|=
literal|"The list subcommand displays the keynames contained within\n"
operator|+
literal|"a particular provider as configured in core-site.xml or\n"
operator|+
literal|"specified with the -provider argument. -metadata displays\n"
operator|+
literal|"the metadata."
decl_stmt|;
DECL|field|metadata
specifier|private
name|boolean
name|metadata
init|=
literal|false
decl_stmt|;
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
name|boolean
name|rc
init|=
literal|true
decl_stmt|;
name|provider
operator|=
name|getKeyProvider
argument_list|()
expr_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"There are no non-transient KeyProviders configured.\n"
operator|+
literal|"Use the -provider option to specify a provider. If you\n"
operator|+
literal|"want to list a transient provider then you must use the\n"
operator|+
literal|"-provider argument."
argument_list|)
expr_stmt|;
name|rc
operator|=
literal|false
expr_stmt|;
block|}
name|metadata
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|LIST_METADATA
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|provider
operator|.
name|getKeys
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Listing keys for KeyProvider: "
operator|+
name|provider
argument_list|)
expr_stmt|;
if|if
condition|(
name|metadata
condition|)
block|{
specifier|final
name|Metadata
index|[]
name|meta
init|=
name|provider
operator|.
name|getKeysMetadata
argument_list|(
name|keys
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|keys
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|meta
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|keys
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|+
literal|" : "
operator|+
name|meta
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|keyName
range|:
name|keys
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Cannot list keys for KeyProvider: "
operator|+
name|provider
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
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
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|DESC
return|;
block|}
block|}
DECL|class|RollCommand
specifier|private
class|class
name|RollCommand
extends|extends
name|Command
block|{
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"roll<keyname> [-provider<provider>] [-help]"
decl_stmt|;
DECL|field|DESC
specifier|public
specifier|static
specifier|final
name|String
name|DESC
init|=
literal|"The roll subcommand creates a new version for the specified key\n"
operator|+
literal|"within the provider indicated using the -provider argument\n"
decl_stmt|;
DECL|field|keyName
name|String
name|keyName
init|=
literal|null
decl_stmt|;
DECL|method|RollCommand (String keyName)
specifier|public
name|RollCommand
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
block|}
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
name|boolean
name|rc
init|=
literal|true
decl_stmt|;
name|provider
operator|=
name|getKeyProvider
argument_list|()
expr_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"There are no valid KeyProviders configured. The key\n"
operator|+
literal|"has not been rolled. Use the -provider option to specify\n"
operator|+
literal|"a provider."
argument_list|)
expr_stmt|;
name|rc
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|keyName
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Please provide a<keyname>.\n"
operator|+
literal|"See the usage description by using -help."
argument_list|)
expr_stmt|;
name|rc
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
try|try
block|{
name|warnIfTransientProvider
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Rolling key version from KeyProvider: "
operator|+
name|provider
operator|+
literal|"\n  for key name: "
operator|+
name|keyName
argument_list|)
expr_stmt|;
try|try
block|{
name|provider
operator|.
name|rollNewVersion
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" has been successfully rolled."
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
name|printProviderWritten
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Cannot roll key: "
operator|+
name|keyName
operator|+
literal|" within KeyProvider: "
operator|+
name|provider
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Cannot roll key: "
operator|+
name|keyName
operator|+
literal|" within KeyProvider: "
operator|+
name|provider
argument_list|)
expr_stmt|;
throw|throw
name|e1
throw|;
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
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|DESC
return|;
block|}
block|}
DECL|class|DeleteCommand
specifier|private
class|class
name|DeleteCommand
extends|extends
name|Command
block|{
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"delete<keyname> [-provider<provider>] [-help]"
decl_stmt|;
DECL|field|DESC
specifier|public
specifier|static
specifier|final
name|String
name|DESC
init|=
literal|"The delete subcommand deletes all versions of the key\n"
operator|+
literal|"specified by the<keyname> argument from within the\n"
operator|+
literal|"provider specified -provider."
decl_stmt|;
DECL|field|keyName
name|String
name|keyName
init|=
literal|null
decl_stmt|;
DECL|field|cont
name|boolean
name|cont
init|=
literal|true
decl_stmt|;
DECL|method|DeleteCommand (String keyName)
specifier|public
name|DeleteCommand
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|keyName
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
name|provider
operator|=
name|getKeyProvider
argument_list|()
expr_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"There are no valid KeyProviders configured. Nothing\n"
operator|+
literal|"was deleted. Use the -provider option to specify a provider."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|keyName
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"There is no keyName specified. Please specify a "
operator|+
literal|"<keyname>. See the usage description with -help."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|interactive
condition|)
block|{
try|try
block|{
name|cont
operator|=
name|ToolRunner
operator|.
name|confirmPrompt
argument_list|(
literal|"You are about to DELETE all versions of "
operator|+
literal|" key: "
operator|+
name|keyName
operator|+
literal|" from KeyProvider "
operator|+
name|provider
operator|+
literal|". Continue?:"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|cont
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Nothing has been be deleted."
argument_list|)
expr_stmt|;
block|}
return|return
name|cont
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" will not be deleted."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
name|warnIfTransientProvider
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Deleting key: "
operator|+
name|keyName
operator|+
literal|" from KeyProvider: "
operator|+
name|provider
argument_list|)
expr_stmt|;
if|if
condition|(
name|cont
condition|)
block|{
try|try
block|{
name|provider
operator|.
name|deleteKey
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" has been successfully deleted."
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
name|printProviderWritten
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" has not been deleted."
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
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
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|DESC
return|;
block|}
block|}
DECL|class|CreateCommand
specifier|private
class|class
name|CreateCommand
extends|extends
name|Command
block|{
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"create<keyname> [-cipher<cipher>] [-size<size>]\n"
operator|+
literal|"                     [-description<description>]\n"
operator|+
literal|"                     [-attr<attribute=value>]\n"
operator|+
literal|"                     [-provider<provider>] [-help]"
decl_stmt|;
DECL|field|DESC
specifier|public
specifier|static
specifier|final
name|String
name|DESC
init|=
literal|"The create subcommand creates a new key for the name specified\n"
operator|+
literal|"by the<keyname> argument within the provider specified by the\n"
operator|+
literal|"-provider argument. You may specify a cipher with the -cipher\n"
operator|+
literal|"argument. The default cipher is currently \"AES/CTR/NoPadding\".\n"
operator|+
literal|"The default keysize is 256. You may specify the requested key\n"
operator|+
literal|"length using the -size argument. Arbitrary attribute=value\n"
operator|+
literal|"style attributes may be specified using the -attr argument.\n"
operator|+
literal|"-attr may be specified multiple times, once per attribute.\n"
decl_stmt|;
DECL|field|keyName
specifier|final
name|String
name|keyName
decl_stmt|;
DECL|field|options
specifier|final
name|Options
name|options
decl_stmt|;
DECL|method|CreateCommand (String keyName, Options options)
specifier|public
name|CreateCommand
parameter_list|(
name|String
name|keyName
parameter_list|,
name|Options
name|options
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
block|}
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
name|boolean
name|rc
init|=
literal|true
decl_stmt|;
name|provider
operator|=
name|getKeyProvider
argument_list|()
expr_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"There are no valid KeyProviders configured. No key\n"
operator|+
literal|" was created. You can use the -provider option to specify\n"
operator|+
literal|" a provider to use."
argument_list|)
expr_stmt|;
name|rc
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|keyName
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Please provide a<keyname>. See the usage description"
operator|+
literal|" with -help."
argument_list|)
expr_stmt|;
name|rc
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
name|warnIfTransientProvider
argument_list|()
expr_stmt|;
try|try
block|{
name|provider
operator|.
name|createKey
argument_list|(
name|keyName
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" has been successfully created."
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
name|printProviderWritten
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidParameterException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" has not been created. "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" has not been created. "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|keyName
operator|+
literal|" has not been created. "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
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
name|USAGE
operator|+
literal|":\n\n"
operator|+
name|DESC
return|;
block|}
block|}
comment|/**    * main() entry point for the KeyShell.  While strictly speaking the    * return is void, it will System.exit() with a return code: 0 is for    * success and 1 for failure.    *    * @param args Command line arguments.    * @throws Exception    */
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
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|KeyShell
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

