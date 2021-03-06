begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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
name|Shell
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_interface
DECL|interface|GetSpaceUsed
specifier|public
interface|interface
name|GetSpaceUsed
block|{
DECL|method|getUsed ()
name|long
name|getUsed
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * The builder class    */
DECL|class|Builder
class|class
name|Builder
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Builder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|klass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|GetSpaceUsed
argument_list|>
name|klass
init|=
literal|null
decl_stmt|;
DECL|field|path
specifier|private
name|File
name|path
init|=
literal|null
decl_stmt|;
DECL|field|interval
specifier|private
name|Long
name|interval
init|=
literal|null
decl_stmt|;
DECL|field|jitter
specifier|private
name|Long
name|jitter
init|=
literal|null
decl_stmt|;
DECL|field|initialUsed
specifier|private
name|Long
name|initialUsed
init|=
literal|null
decl_stmt|;
DECL|field|cons
specifier|private
name|Constructor
argument_list|<
name|?
extends|extends
name|GetSpaceUsed
argument_list|>
name|cons
decl_stmt|;
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|setConf (Configuration conf)
specifier|public
name|Builder
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getInterval ()
specifier|public
name|long
name|getInterval
parameter_list|()
block|{
if|if
condition|(
name|interval
operator|!=
literal|null
condition|)
block|{
return|return
name|interval
return|;
block|}
name|long
name|result
init|=
name|CommonConfigurationKeys
operator|.
name|FS_DU_INTERVAL_DEFAULT
decl_stmt|;
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
return|return
name|conf
operator|.
name|getLong
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_DU_INTERVAL_KEY
argument_list|,
name|result
argument_list|)
return|;
block|}
DECL|method|setInterval (long interval)
specifier|public
name|Builder
name|setInterval
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getKlass ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|GetSpaceUsed
argument_list|>
name|getKlass
parameter_list|()
block|{
if|if
condition|(
name|klass
operator|!=
literal|null
condition|)
block|{
return|return
name|klass
return|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|GetSpaceUsed
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|result
operator|=
name|WindowsGetSpaceUsed
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|DU
operator|.
name|class
expr_stmt|;
block|}
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
return|return
name|conf
operator|.
name|getClass
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_GETSPACEUSED_CLASSNAME
argument_list|,
name|result
argument_list|,
name|GetSpaceUsed
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|setKlass (Class<? extends GetSpaceUsed> klass)
specifier|public
name|Builder
name|setKlass
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|GetSpaceUsed
argument_list|>
name|klass
parameter_list|)
block|{
name|this
operator|.
name|klass
operator|=
name|klass
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getPath ()
specifier|public
name|File
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|setPath (File path)
specifier|public
name|Builder
name|setPath
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getInitialUsed ()
specifier|public
name|long
name|getInitialUsed
parameter_list|()
block|{
if|if
condition|(
name|initialUsed
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|initialUsed
return|;
block|}
DECL|method|setInitialUsed (long initialUsed)
specifier|public
name|Builder
name|setInitialUsed
parameter_list|(
name|long
name|initialUsed
parameter_list|)
block|{
name|this
operator|.
name|initialUsed
operator|=
name|initialUsed
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getJitter ()
specifier|public
name|long
name|getJitter
parameter_list|()
block|{
if|if
condition|(
name|jitter
operator|==
literal|null
condition|)
block|{
name|Configuration
name|configuration
init|=
name|this
operator|.
name|conf
decl_stmt|;
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
return|return
name|CommonConfigurationKeys
operator|.
name|FS_GETSPACEUSED_JITTER_DEFAULT
return|;
block|}
return|return
name|configuration
operator|.
name|getLong
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_GETSPACEUSED_JITTER_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|FS_GETSPACEUSED_JITTER_DEFAULT
argument_list|)
return|;
block|}
return|return
name|jitter
return|;
block|}
DECL|method|setJitter (Long jit)
specifier|public
name|Builder
name|setJitter
parameter_list|(
name|Long
name|jit
parameter_list|)
block|{
name|this
operator|.
name|jitter
operator|=
name|jit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getCons ()
specifier|public
name|Constructor
argument_list|<
name|?
extends|extends
name|GetSpaceUsed
argument_list|>
name|getCons
parameter_list|()
block|{
return|return
name|cons
return|;
block|}
DECL|method|setCons (Constructor<? extends GetSpaceUsed> cons)
specifier|public
name|void
name|setCons
parameter_list|(
name|Constructor
argument_list|<
name|?
extends|extends
name|GetSpaceUsed
argument_list|>
name|cons
parameter_list|)
block|{
name|this
operator|.
name|cons
operator|=
name|cons
expr_stmt|;
block|}
DECL|method|build ()
specifier|public
name|GetSpaceUsed
name|build
parameter_list|()
throws|throws
name|IOException
block|{
name|GetSpaceUsed
name|getSpaceUsed
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cons
operator|==
literal|null
condition|)
block|{
name|cons
operator|=
name|getKlass
argument_list|()
operator|.
name|getConstructor
argument_list|(
name|Builder
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|getSpaceUsed
operator|=
name|cons
operator|.
name|newInstance
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error trying to create an instance of "
operator|+
name|getKlass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error trying to create "
operator|+
name|getKlass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error trying to create "
operator|+
name|getKlass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Doesn't look like the class "
operator|+
name|getKlass
argument_list|()
operator|+
literal|" have the needed constructor"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// If there were any exceptions then du will be null.
comment|// Construct our best guess fallback.
if|if
condition|(
name|getSpaceUsed
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|getSpaceUsed
operator|=
operator|new
name|WindowsGetSpaceUsed
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getSpaceUsed
operator|=
operator|new
name|DU
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Call init after classes constructors have finished.
if|if
condition|(
name|getSpaceUsed
operator|instanceof
name|CachingGetSpaceUsed
condition|)
block|{
operator|(
operator|(
name|CachingGetSpaceUsed
operator|)
name|getSpaceUsed
operator|)
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
return|return
name|getSpaceUsed
return|;
block|}
block|}
block|}
end_interface

end_unit

