begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|Shell
operator|.
name|ShellCommandExecutor
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|CommonConfigurationKeys
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
comment|/**  * This class implements the {@link DNSToSwitchMapping} interface using a   * script configured via the  * {@link CommonConfigurationKeys#NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY} option.  *<p>  * It contains a static class<code>RawScriptBasedMapping</code> that performs  * the work: reading the configuration parameters, executing any defined  * script, handling errors and such like. The outer  * class extends {@link CachedDNSToSwitchMapping} to cache the delegated  * queries.  *<p>  * This DNS mapper's {@link #isSingleSwitch()} predicate returns  * true if and only if a script is defined.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ScriptBasedMapping
specifier|public
class|class
name|ScriptBasedMapping
extends|extends
name|CachedDNSToSwitchMapping
block|{
comment|/**    * Minimum number of arguments: {@value}    */
DECL|field|MIN_ALLOWABLE_ARGS
specifier|static
specifier|final
name|int
name|MIN_ALLOWABLE_ARGS
init|=
literal|1
decl_stmt|;
comment|/**    * Default number of arguments: {@value}    */
DECL|field|DEFAULT_ARG_COUNT
specifier|static
specifier|final
name|int
name|DEFAULT_ARG_COUNT
init|=
name|CommonConfigurationKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_DEFAULT
decl_stmt|;
comment|/**    * key to the script filename {@value}    */
DECL|field|SCRIPT_FILENAME_KEY
specifier|static
specifier|final
name|String
name|SCRIPT_FILENAME_KEY
init|=
name|CommonConfigurationKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
decl_stmt|;
comment|/**    * key to the argument count that the script supports    * {@value}    */
DECL|field|SCRIPT_ARG_COUNT_KEY
specifier|static
specifier|final
name|String
name|SCRIPT_ARG_COUNT_KEY
init|=
name|CommonConfigurationKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_KEY
decl_stmt|;
comment|/**    * Text used in the {@link #toString()} method if there is no string    * {@value}    */
DECL|field|NO_SCRIPT
specifier|public
specifier|static
specifier|final
name|String
name|NO_SCRIPT
init|=
literal|"no script"
decl_stmt|;
comment|/**    * Create an instance with the default configuration.    *<p>    * Calling {@link #setConf(Configuration)} will trigger a    * re-evaluation of the configuration settings and so be used to    * set up the mapping script.    *    */
DECL|method|ScriptBasedMapping ()
specifier|public
name|ScriptBasedMapping
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|RawScriptBasedMapping
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an instance from the given raw mapping    * @param rawMap raw DNSTOSwithMapping    */
DECL|method|ScriptBasedMapping (DNSToSwitchMapping rawMap)
specifier|public
name|ScriptBasedMapping
parameter_list|(
name|DNSToSwitchMapping
name|rawMap
parameter_list|)
block|{
name|super
argument_list|(
name|rawMap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an instance from the given configuration    * @param conf configuration    */
DECL|method|ScriptBasedMapping (Configuration conf)
specifier|public
name|ScriptBasedMapping
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the cached mapping and convert it to its real type    * @return the inner raw script mapping.    */
DECL|method|getRawMapping ()
specifier|private
name|RawScriptBasedMapping
name|getRawMapping
parameter_list|()
block|{
return|return
operator|(
name|RawScriptBasedMapping
operator|)
name|rawMapping
return|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|getRawMapping
argument_list|()
operator|.
name|getConf
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"script-based mapping with "
operator|+
name|getRawMapping
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * This will get called in the superclass constructor, so a check is needed    * to ensure that the raw mapping is defined before trying to relaying a null    * configuration.    * @param conf    */
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|getRawMapping
argument_list|()
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is the uncached script mapping that is fed into the cache managed    * by the superclass {@link CachedDNSToSwitchMapping}    */
DECL|class|RawScriptBasedMapping
specifier|protected
specifier|static
class|class
name|RawScriptBasedMapping
extends|extends
name|AbstractDNSToSwitchMapping
block|{
DECL|field|scriptName
specifier|private
name|String
name|scriptName
decl_stmt|;
DECL|field|maxArgs
specifier|private
name|int
name|maxArgs
decl_stmt|;
comment|//max hostnames per call of the script
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
name|ScriptBasedMapping
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Set the configuration and extract the configuration parameters of interest      * @param conf the new configuration      */
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|scriptName
operator|=
name|conf
operator|.
name|get
argument_list|(
name|SCRIPT_FILENAME_KEY
argument_list|)
expr_stmt|;
name|maxArgs
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|SCRIPT_ARG_COUNT_KEY
argument_list|,
name|DEFAULT_ARG_COUNT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scriptName
operator|=
literal|null
expr_stmt|;
name|maxArgs
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**      * Constructor. The mapping is not ready to use until      * {@link #setConf(Configuration)} has been called      */
DECL|method|RawScriptBasedMapping ()
specifier|public
name|RawScriptBasedMapping
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|resolve (List<String> names)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|m
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|m
return|;
block|}
if|if
condition|(
name|scriptName
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|m
operator|.
name|add
argument_list|(
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
name|String
name|output
init|=
name|runResolveCommand
argument_list|(
name|names
argument_list|,
name|scriptName
argument_list|)
decl_stmt|;
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|allSwitchInfo
init|=
operator|new
name|StringTokenizer
argument_list|(
name|output
argument_list|)
decl_stmt|;
while|while
condition|(
name|allSwitchInfo
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|switchInfo
init|=
name|allSwitchInfo
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|m
operator|.
name|add
argument_list|(
name|switchInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|m
operator|.
name|size
argument_list|()
operator|!=
name|names
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// invalid number of entries returned by the script
name|LOG
operator|.
name|error
argument_list|(
literal|"Script "
operator|+
name|scriptName
operator|+
literal|" returned "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|m
operator|.
name|size
argument_list|()
argument_list|)
operator|+
literal|" values when "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
operator|+
literal|" were expected."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|// an error occurred. return null to signify this.
comment|// (exn was already logged in runResolveCommand)
return|return
literal|null
return|;
block|}
return|return
name|m
return|;
block|}
comment|/**      * Build and execute the resolution command. The command is      * executed in the directory specified by the system property      * "user.dir" if set; otherwise the current working directory is used      * @param args a list of arguments      * @return null if the number of arguments is out of range,      * or the output of the command.      */
DECL|method|runResolveCommand (List<String> args, String commandScriptName)
specifier|protected
name|String
name|runResolveCommand
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|commandScriptName
parameter_list|)
block|{
name|int
name|loopCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StringBuilder
name|allOutput
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numProcessed
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|maxArgs
operator|<
name|MIN_ALLOWABLE_ARGS
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid value "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|maxArgs
argument_list|)
operator|+
literal|" for "
operator|+
name|SCRIPT_ARG_COUNT_KEY
operator|+
literal|"; must be>= "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|MIN_ALLOWABLE_ARGS
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
while|while
condition|(
name|numProcessed
operator|!=
name|args
operator|.
name|size
argument_list|()
condition|)
block|{
name|int
name|start
init|=
name|maxArgs
operator|*
name|loopCount
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|cmdList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|cmdList
operator|.
name|add
argument_list|(
name|commandScriptName
argument_list|)
expr_stmt|;
for|for
control|(
name|numProcessed
operator|=
name|start
init|;
name|numProcessed
operator|<
operator|(
name|start
operator|+
name|maxArgs
operator|)
operator|&&
name|numProcessed
operator|<
name|args
operator|.
name|size
argument_list|()
condition|;
name|numProcessed
operator|++
control|)
block|{
name|cmdList
operator|.
name|add
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|numProcessed
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|File
name|dir
init|=
literal|null
decl_stmt|;
name|String
name|userDir
decl_stmt|;
if|if
condition|(
operator|(
name|userDir
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|userDir
argument_list|)
expr_stmt|;
block|}
name|ShellCommandExecutor
name|s
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|cmdList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|cmdList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|dir
argument_list|)
decl_stmt|;
try|try
block|{
name|s
operator|.
name|execute
argument_list|()
expr_stmt|;
name|allOutput
operator|.
name|append
argument_list|(
name|s
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception running "
operator|+
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|loopCount
operator|++
expr_stmt|;
block|}
return|return
name|allOutput
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Declare that the mapper is single-switched if a script was not named      * in the configuration.      * @return true iff there is no script      */
annotation|@
name|Override
DECL|method|isSingleSwitch ()
specifier|public
name|boolean
name|isSingleSwitch
parameter_list|()
block|{
return|return
name|scriptName
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|scriptName
operator|!=
literal|null
condition|?
operator|(
literal|"script "
operator|+
name|scriptName
operator|)
else|:
name|NO_SCRIPT
return|;
block|}
annotation|@
name|Override
DECL|method|reloadCachedMappings ()
specifier|public
name|void
name|reloadCachedMappings
parameter_list|()
block|{
comment|// Nothing to do here, since RawScriptBasedMapping has no cache, and
comment|// does not inherit from CachedDNSToSwitchMapping
block|}
annotation|@
name|Override
DECL|method|reloadCachedMappings (List<String> names)
specifier|public
name|void
name|reloadCachedMappings
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
comment|// Nothing to do here, since RawScriptBasedMapping has no cache, and
comment|// does not inherit from CachedDNSToSwitchMapping
block|}
block|}
block|}
end_class

end_unit

