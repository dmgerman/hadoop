begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
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
name|Field
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
name|Method
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|metrics2
operator|.
name|MetricsCollector
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
name|metrics2
operator|.
name|MetricsException
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
name|metrics2
operator|.
name|MetricsInfo
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
name|metrics2
operator|.
name|MetricsSource
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metric
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
name|metrics2
operator|.
name|annotation
operator|.
name|Metrics
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

begin_comment
comment|/**  * Helper class to build {@link MetricsSource} object from annotations.  *<p>  * For a given source object:  *<ul>  *<li>Sets the {@link Field}s annotated with {@link Metric} to  * {@link MutableMetric} and adds it to the {@link MetricsRegistry}.</li>  *<li>  * For {@link Method}s annotated with {@link Metric} creates  * {@link MutableMetric} and adds it to the {@link MetricsRegistry}.</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|MetricsSourceBuilder
specifier|public
class|class
name|MetricsSourceBuilder
block|{
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
name|MetricsSourceBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Object
name|source
decl_stmt|;
DECL|field|factory
specifier|private
specifier|final
name|MutableMetricsFactory
name|factory
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|field|info
specifier|private
name|MetricsInfo
name|info
decl_stmt|;
DECL|field|hasAtMetric
specifier|private
name|boolean
name|hasAtMetric
init|=
literal|false
decl_stmt|;
DECL|field|hasRegistry
specifier|private
name|boolean
name|hasRegistry
init|=
literal|false
decl_stmt|;
DECL|method|MetricsSourceBuilder (Object source, MutableMetricsFactory factory)
name|MetricsSourceBuilder
parameter_list|(
name|Object
name|source
parameter_list|,
name|MutableMetricsFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|checkNotNull
argument_list|(
name|source
argument_list|,
literal|"source"
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|checkNotNull
argument_list|(
name|factory
argument_list|,
literal|"mutable metrics factory"
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|cls
init|=
name|source
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|registry
operator|=
name|initRegistry
argument_list|(
name|source
argument_list|)
expr_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|ReflectionUtils
operator|.
name|getDeclaredFieldsIncludingInherited
argument_list|(
name|cls
argument_list|)
control|)
block|{
name|add
argument_list|(
name|source
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Method
name|method
range|:
name|ReflectionUtils
operator|.
name|getDeclaredMethodsIncludingInherited
argument_list|(
name|cls
argument_list|)
control|)
block|{
name|add
argument_list|(
name|source
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|build ()
specifier|public
name|MetricsSource
name|build
parameter_list|()
block|{
if|if
condition|(
name|source
operator|instanceof
name|MetricsSource
condition|)
block|{
if|if
condition|(
name|hasAtMetric
operator|&&
operator|!
name|hasRegistry
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Hybrid metrics: registry required."
argument_list|)
throw|;
block|}
return|return
operator|(
name|MetricsSource
operator|)
name|source
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|hasAtMetric
condition|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"No valid @Metric annotation found."
argument_list|)
throw|;
block|}
return|return
operator|new
name|MetricsSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|getMetrics
parameter_list|(
name|MetricsCollector
name|builder
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|registry
operator|.
name|snapshot
argument_list|(
name|builder
operator|.
name|addRecord
argument_list|(
name|registry
operator|.
name|info
argument_list|()
argument_list|)
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|info ()
specifier|public
name|MetricsInfo
name|info
parameter_list|()
block|{
return|return
name|info
return|;
block|}
DECL|method|initRegistry (Object source)
specifier|private
name|MetricsRegistry
name|initRegistry
parameter_list|(
name|Object
name|source
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cls
init|=
name|source
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|MetricsRegistry
name|r
init|=
literal|null
decl_stmt|;
comment|// Get the registry if it already exists.
for|for
control|(
name|Field
name|field
range|:
name|ReflectionUtils
operator|.
name|getDeclaredFieldsIncludingInherited
argument_list|(
name|cls
argument_list|)
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|!=
name|MetricsRegistry
operator|.
name|class
condition|)
continue|continue;
try|try
block|{
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|MetricsRegistry
operator|)
name|field
operator|.
name|get
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|hasRegistry
operator|=
name|r
operator|!=
literal|null
expr_stmt|;
break|break;
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
literal|"Error accessing field "
operator|+
name|field
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
comment|// Create a new registry according to annotation
for|for
control|(
name|Annotation
name|annotation
range|:
name|cls
operator|.
name|getAnnotations
argument_list|()
control|)
block|{
if|if
condition|(
name|annotation
operator|instanceof
name|Metrics
condition|)
block|{
name|Metrics
name|ma
init|=
operator|(
name|Metrics
operator|)
name|annotation
decl_stmt|;
name|info
operator|=
name|factory
operator|.
name|getInfo
argument_list|(
name|cls
argument_list|,
name|ma
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|setContext
argument_list|(
name|ma
operator|.
name|context
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|r
operator|==
literal|null
condition|)
return|return
operator|new
name|MetricsRegistry
argument_list|(
name|cls
operator|.
name|getSimpleName
argument_list|()
argument_list|)
return|;
return|return
name|r
return|;
block|}
comment|/**    * Change the declared field {@code field} in {@code source} Object to    * {@link MutableMetric}    */
DECL|method|add (Object source, Field field)
specifier|private
name|void
name|add
parameter_list|(
name|Object
name|source
parameter_list|,
name|Field
name|field
parameter_list|)
block|{
for|for
control|(
name|Annotation
name|annotation
range|:
name|field
operator|.
name|getAnnotations
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|annotation
operator|instanceof
name|Metric
operator|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
comment|// skip fields already set
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|get
argument_list|(
name|source
argument_list|)
operator|!=
literal|null
condition|)
continue|continue;
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
literal|"Error accessing field "
operator|+
name|field
operator|+
literal|" annotated with"
operator|+
name|annotation
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|MutableMetric
name|mutable
init|=
name|factory
operator|.
name|newForField
argument_list|(
name|field
argument_list|,
operator|(
name|Metric
operator|)
name|annotation
argument_list|,
name|registry
argument_list|)
decl_stmt|;
if|if
condition|(
name|mutable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|field
operator|.
name|set
argument_list|(
name|source
argument_list|,
name|mutable
argument_list|)
expr_stmt|;
comment|// Set the source field to MutableMetric
name|hasAtMetric
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MetricsException
argument_list|(
literal|"Error setting field "
operator|+
name|field
operator|+
literal|" annotated with "
operator|+
name|annotation
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/** Add {@link MutableMetric} for a method annotated with {@link Metric} */
DECL|method|add (Object source, Method method)
specifier|private
name|void
name|add
parameter_list|(
name|Object
name|source
parameter_list|,
name|Method
name|method
parameter_list|)
block|{
for|for
control|(
name|Annotation
name|annotation
range|:
name|method
operator|.
name|getAnnotations
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|annotation
operator|instanceof
name|Metric
operator|)
condition|)
block|{
continue|continue;
block|}
name|factory
operator|.
name|newForMethod
argument_list|(
name|source
argument_list|,
name|method
argument_list|,
operator|(
name|Metric
operator|)
name|annotation
argument_list|,
name|registry
argument_list|)
expr_stmt|;
name|hasAtMetric
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

