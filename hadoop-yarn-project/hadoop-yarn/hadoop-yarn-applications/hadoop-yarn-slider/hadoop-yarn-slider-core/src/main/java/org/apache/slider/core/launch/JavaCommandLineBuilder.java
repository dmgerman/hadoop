begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.launch
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|launch
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|BadConfigException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * Command line builder purely for the Java CLI.  * Some of the<code>define</code> methods are designed to work with Hadoop tool and  * Slider launcher applications.  */
end_comment

begin_class
DECL|class|JavaCommandLineBuilder
specifier|public
class|class
name|JavaCommandLineBuilder
extends|extends
name|CommandLineBuilder
block|{
DECL|method|JavaCommandLineBuilder ()
specifier|public
name|JavaCommandLineBuilder
parameter_list|()
block|{
name|add
argument_list|(
name|getJavaBinary
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the java binary. This is called in the constructor so don't try and    * do anything other than return a constant.    * @return the path to the Java binary    */
DECL|method|getJavaBinary ()
specifier|protected
name|String
name|getJavaBinary
parameter_list|()
block|{
return|return
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|JAVA_HOME
operator|.
name|$$
argument_list|()
operator|+
literal|"/bin/java"
return|;
block|}
comment|/**    * Set the size of the heap if a non-empty heap is passed in.     * @param heap empty string or something like "128M" ,"1G" etc. The value is    * trimmed.    */
DECL|method|setJVMHeap (String heap)
specifier|public
name|void
name|setJVMHeap
parameter_list|(
name|String
name|heap
parameter_list|)
block|{
if|if
condition|(
name|SliderUtils
operator|.
name|isSet
argument_list|(
name|heap
argument_list|)
condition|)
block|{
name|add
argument_list|(
literal|"-Xmx"
operator|+
name|heap
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Turn Java assertions on    */
DECL|method|enableJavaAssertions ()
specifier|public
name|void
name|enableJavaAssertions
parameter_list|()
block|{
name|add
argument_list|(
literal|"-ea"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"-esa"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a system property definition -must be used before setting the main entry point    * @param property    * @param value    */
DECL|method|sysprop (String property, String value)
specifier|public
name|void
name|sysprop
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|property
operator|!=
literal|null
argument_list|,
literal|"null property name"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|value
operator|!=
literal|null
argument_list|,
literal|"null value"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"-D"
operator|+
name|property
operator|+
literal|"="
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|forceIPv4 ()
specifier|public
name|JavaCommandLineBuilder
name|forceIPv4
parameter_list|()
block|{
name|sysprop
argument_list|(
literal|"java.net.preferIPv4Stack"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|headless ()
specifier|public
name|JavaCommandLineBuilder
name|headless
parameter_list|()
block|{
name|sysprop
argument_list|(
literal|"java.awt.headless"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addConfOption (Configuration conf, String key)
specifier|public
name|boolean
name|addConfOption
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|defineIfSet
argument_list|(
name|key
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Add a varargs list of configuration parameters âif they are present    * @param conf configuration source    * @param keys keys    */
DECL|method|addConfOptions (Configuration conf, String... keys)
specifier|public
name|void
name|addConfOptions
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
modifier|...
name|keys
parameter_list|)
block|{
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|addConfOption
argument_list|(
name|conf
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add all configuration options which match the prefix    * @param conf configuration    * @param prefix prefix, e.g {@code "slider."}    * @return the number of entries copied    */
DECL|method|addPrefixedConfOptions (Configuration conf, String prefix)
specifier|public
name|int
name|addPrefixedConfOptions
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|int
name|copied
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|define
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|copied
operator|++
expr_stmt|;
block|}
block|}
return|return
name|copied
return|;
block|}
comment|/**    * Ass a configuration option to the command line of  the application    * @param conf configuration    * @param key key    * @param defVal default value    * @return the resolved configuration option    * @throws IllegalArgumentException if key is null or the looked up value    * is null (that is: the argument is missing and devVal was null.    */
DECL|method|addConfOptionToCLI (Configuration conf, String key, String defVal)
specifier|public
name|String
name|addConfOptionToCLI
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|defVal
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|key
operator|!=
literal|null
argument_list|,
literal|"null key"
argument_list|)
expr_stmt|;
name|String
name|val
init|=
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|defVal
argument_list|)
decl_stmt|;
name|define
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|val
return|;
block|}
comment|/**    * Add a<code>-D key=val</code> command to the CLI. This is very Hadoop API    * @param key key    * @param val value    * @throws IllegalArgumentException if either argument is null    */
DECL|method|define (String key, String val)
specifier|public
name|void
name|define
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|key
operator|!=
literal|null
argument_list|,
literal|"null key"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|val
operator|!=
literal|null
argument_list|,
literal|"null value"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"-D"
argument_list|,
name|key
operator|+
literal|"="
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a<code>-D key=val</code> command to the CLI if<code>val</code>    * is not null    * @param key key    * @param val value    */
DECL|method|defineIfSet (String key, String val)
specifier|public
name|boolean
name|defineIfSet
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|key
operator|!=
literal|null
argument_list|,
literal|"null key"
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|define
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Add a mandatory config option    * @param conf configuration    * @param key key    * @throws BadConfigException if the key is missing    */
DECL|method|addMandatoryConfOption (Configuration conf, String key)
specifier|public
name|void
name|addMandatoryConfOption
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|BadConfigException
block|{
if|if
condition|(
operator|!
name|addConfOption
argument_list|(
name|conf
argument_list|,
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadConfigException
argument_list|(
literal|"Missing configuration option: "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

