begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|ReflectionUtils
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
name|ImmutableMap
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

begin_comment
comment|/**  * This class parses the configured list of fencing methods, and  * is responsible for trying each one in turn while logging informative  * output.<p>  *   * The fencing methods are configured as a carriage-return separated list.  * Each line in the list is of the form:<p>  *<code>com.example.foo.MyMethod(arg string)</code>  * or  *<code>com.example.foo.MyMethod</code>  * The class provided must implement the {@link FenceMethod} interface.  * The fencing methods that ship with Hadoop may also be referred to  * by shortened names:<p>  *<ul>  *<li><code>shell(/path/to/some/script.sh args...)</code></li>  *<li><code>sshfence(...)</code> (see {@link SshFenceByTcpPort})  *</ul>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|NodeFencer
specifier|public
class|class
name|NodeFencer
block|{
DECL|field|CONF_METHODS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CONF_METHODS_KEY
init|=
literal|"dfs.ha.fencing.methods"
decl_stmt|;
DECL|field|CLASS_RE
specifier|private
specifier|static
specifier|final
name|String
name|CLASS_RE
init|=
literal|"([a-zA-Z0-9\\.\\$]+)"
decl_stmt|;
DECL|field|CLASS_WITH_ARGUMENT
specifier|private
specifier|static
specifier|final
name|Pattern
name|CLASS_WITH_ARGUMENT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|CLASS_RE
operator|+
literal|"\\((.+?)\\)"
argument_list|)
decl_stmt|;
DECL|field|CLASS_WITHOUT_ARGUMENT
specifier|private
specifier|static
specifier|final
name|Pattern
name|CLASS_WITHOUT_ARGUMENT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|CLASS_RE
argument_list|)
decl_stmt|;
DECL|field|HASH_COMMENT_RE
specifier|private
specifier|static
specifier|final
name|Pattern
name|HASH_COMMENT_RE
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"#.*$"
argument_list|)
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NodeFencer
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Standard fencing methods included with Hadoop.    */
DECL|field|STANDARD_METHODS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FenceMethod
argument_list|>
argument_list|>
name|STANDARD_METHODS
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Class
argument_list|<
name|?
extends|extends
name|FenceMethod
argument_list|>
decl|>
name|of
argument_list|(
literal|"shell"
argument_list|,
name|ShellCommandFencer
operator|.
name|class
argument_list|,
literal|"sshfence"
argument_list|,
name|SshFenceByTcpPort
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|methods
specifier|private
specifier|final
name|List
argument_list|<
name|FenceMethodWithArg
argument_list|>
name|methods
decl_stmt|;
DECL|method|NodeFencer (Configuration conf)
specifier|public
name|NodeFencer
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|this
operator|.
name|methods
operator|=
name|parseMethods
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|create (Configuration conf)
specifier|public
specifier|static
name|NodeFencer
name|create
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|String
name|confStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|CONF_METHODS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|confStr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|NodeFencer
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|fence (InetSocketAddress serviceAddr)
specifier|public
name|boolean
name|fence
parameter_list|(
name|InetSocketAddress
name|serviceAddr
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"====== Beginning Service Fencing Process... ======"
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FenceMethodWithArg
name|method
range|:
name|methods
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying method "
operator|+
operator|(
operator|++
name|i
operator|)
operator|+
literal|"/"
operator|+
name|methods
operator|.
name|size
argument_list|()
operator|+
literal|": "
operator|+
name|method
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|method
operator|.
name|method
operator|.
name|tryFence
argument_list|(
name|serviceAddr
argument_list|,
name|method
operator|.
name|arg
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"====== Fencing successful by method "
operator|+
name|method
operator|+
literal|" ======"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|BadFencingConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Fencing method "
operator|+
name|method
operator|+
literal|" misconfigured"
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Fencing method "
operator|+
name|method
operator|+
literal|" failed with an unexpected error."
argument_list|,
name|t
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Fencing method "
operator|+
name|method
operator|+
literal|" was unsuccessful."
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to fence service by any configured method."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|parseMethods (Configuration conf)
specifier|private
specifier|static
name|List
argument_list|<
name|FenceMethodWithArg
argument_list|>
name|parseMethods
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|String
name|confStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|CONF_METHODS_KEY
argument_list|)
decl_stmt|;
name|String
index|[]
name|lines
init|=
name|confStr
operator|.
name|split
argument_list|(
literal|"\\s*\n\\s*"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FenceMethodWithArg
argument_list|>
name|methods
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
name|line
operator|=
name|HASH_COMMENT_RE
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|line
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|methods
operator|.
name|add
argument_list|(
name|parseMethod
argument_list|(
name|conf
argument_list|,
name|line
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|methods
return|;
block|}
DECL|method|parseMethod (Configuration conf, String line)
specifier|private
specifier|static
name|FenceMethodWithArg
name|parseMethod
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|line
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|Matcher
name|m
decl_stmt|;
if|if
condition|(
operator|(
name|m
operator|=
name|CLASS_WITH_ARGUMENT
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|className
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|arg
init|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
name|createFenceMethod
argument_list|(
name|conf
argument_list|,
name|className
argument_list|,
name|arg
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|m
operator|=
name|CLASS_WITHOUT_ARGUMENT
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|String
name|className
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|createFenceMethod
argument_list|(
name|conf
argument_list|,
name|className
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|BadFencingConfigurationException
argument_list|(
literal|"Unable to parse line: '"
operator|+
name|line
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
DECL|method|createFenceMethod ( Configuration conf, String clazzName, String arg)
specifier|private
specifier|static
name|FenceMethodWithArg
name|createFenceMethod
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|clazzName
parameter_list|,
name|String
name|arg
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
decl_stmt|;
try|try
block|{
comment|// See if it's a short name for one of the built-in methods
name|clazz
operator|=
name|STANDARD_METHODS
operator|.
name|get
argument_list|(
name|clazzName
argument_list|)
expr_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
comment|// Try to instantiate the user's custom method
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|clazzName
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadFencingConfigurationException
argument_list|(
literal|"Could not find configured fencing method "
operator|+
name|clazzName
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Check that it implements the right interface
if|if
condition|(
operator|!
name|FenceMethod
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadFencingConfigurationException
argument_list|(
literal|"Class "
operator|+
name|clazzName
operator|+
literal|" does not implement FenceMethod"
argument_list|)
throw|;
block|}
name|FenceMethod
name|method
init|=
operator|(
name|FenceMethod
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|method
operator|.
name|checkArgs
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
operator|new
name|FenceMethodWithArg
argument_list|(
name|method
argument_list|,
name|arg
argument_list|)
return|;
block|}
DECL|class|FenceMethodWithArg
specifier|private
specifier|static
class|class
name|FenceMethodWithArg
block|{
DECL|field|method
specifier|private
specifier|final
name|FenceMethod
name|method
decl_stmt|;
DECL|field|arg
specifier|private
specifier|final
name|String
name|arg
decl_stmt|;
DECL|method|FenceMethodWithArg (FenceMethod method, String arg)
specifier|private
name|FenceMethodWithArg
parameter_list|(
name|FenceMethod
name|method
parameter_list|,
name|String
name|arg
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|arg
operator|=
name|arg
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|method
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|"("
operator|+
name|arg
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

end_unit

