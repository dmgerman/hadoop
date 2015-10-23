begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ContextFactory.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
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
name|InputStream
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
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|metrics
operator|.
name|spi
operator|.
name|NullContext
import|;
end_import

begin_comment
comment|/**  * Factory class for creating MetricsContext objects.  To obtain an instance  * of this class, use the static<code>getFactory()</code> method.  *  * @deprecated Use org.apache.hadoop.metrics2 package instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ContextFactory
specifier|public
class|class
name|ContextFactory
block|{
DECL|field|PROPERTIES_FILE
specifier|private
specifier|static
specifier|final
name|String
name|PROPERTIES_FILE
init|=
literal|"/hadoop-metrics.properties"
decl_stmt|;
DECL|field|CONTEXT_CLASS_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|CONTEXT_CLASS_SUFFIX
init|=
literal|".class"
decl_stmt|;
DECL|field|DEFAULT_CONTEXT_CLASSNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CONTEXT_CLASSNAME
init|=
literal|"org.apache.hadoop.metrics.spi.NullContext"
decl_stmt|;
DECL|field|theFactory
specifier|private
specifier|static
name|ContextFactory
name|theFactory
init|=
literal|null
decl_stmt|;
DECL|field|attributeMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributeMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|contextMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MetricsContext
argument_list|>
name|contextMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MetricsContext
argument_list|>
argument_list|()
decl_stmt|;
comment|// Used only when contexts, or the ContextFactory itself, cannot be
comment|// created.
DECL|field|nullContextMap
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|MetricsContext
argument_list|>
name|nullContextMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MetricsContext
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Creates a new instance of ContextFactory */
DECL|method|ContextFactory ()
specifier|protected
name|ContextFactory
parameter_list|()
block|{   }
comment|/**    * Returns the value of the named attribute, or null if there is no     * attribute of that name.    *    * @param attributeName the attribute name    * @return the attribute value    */
DECL|method|getAttribute (String attributeName)
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
return|return
name|attributeMap
operator|.
name|get
argument_list|(
name|attributeName
argument_list|)
return|;
block|}
comment|/**    * Returns the names of all the factory's attributes.    *     * @return the attribute names    */
DECL|method|getAttributeNames ()
specifier|public
name|String
index|[]
name|getAttributeNames
parameter_list|()
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|attributeMap
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|// for (String attributeName : attributeMap.keySet()) {
name|Iterator
name|it
init|=
name|attributeMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
index|[
name|i
operator|++
index|]
operator|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Sets the named factory attribute to the specified value, creating it    * if it did not already exist.  If the value is null, this is the same as    * calling removeAttribute.    *    * @param attributeName the attribute name    * @param value the new attribute value    */
DECL|method|setAttribute (String attributeName, Object value)
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|attributeMap
operator|.
name|put
argument_list|(
name|attributeName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Removes the named attribute if it exists.    *    * @param attributeName the attribute name    */
DECL|method|removeAttribute (String attributeName)
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
name|attributeMap
operator|.
name|remove
argument_list|(
name|attributeName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the named MetricsContext instance, constructing it if necessary     * using the factory's current configuration attributes.<p/>    *     * When constructing the instance, if the factory property     *<i>contextName</i>.class</code> exists,     * its value is taken to be the name of the class to instantiate.  Otherwise,    * the default is to create an instance of     *<code>org.apache.hadoop.metrics.spi.NullContext</code>, which is a     * dummy "no-op" context which will cause all metric data to be discarded.    *     * @param contextName the name of the context    * @return the named MetricsContext    */
DECL|method|getContext (String refName, String contextName)
specifier|public
specifier|synchronized
name|MetricsContext
name|getContext
parameter_list|(
name|String
name|refName
parameter_list|,
name|String
name|contextName
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
block|{
name|MetricsContext
name|metricsContext
init|=
name|contextMap
operator|.
name|get
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
name|metricsContext
operator|==
literal|null
condition|)
block|{
name|String
name|classNameAttribute
init|=
name|refName
operator|+
name|CONTEXT_CLASS_SUFFIX
decl_stmt|;
name|String
name|className
init|=
operator|(
name|String
operator|)
name|getAttribute
argument_list|(
name|classNameAttribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
name|className
operator|=
name|DEFAULT_CONTEXT_CLASSNAME
expr_stmt|;
block|}
name|Class
name|contextClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|metricsContext
operator|=
operator|(
name|MetricsContext
operator|)
name|contextClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|metricsContext
operator|.
name|init
argument_list|(
name|contextName
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|contextMap
operator|.
name|put
argument_list|(
name|contextName
argument_list|,
name|metricsContext
argument_list|)
expr_stmt|;
block|}
return|return
name|metricsContext
return|;
block|}
DECL|method|getContext (String contextName)
specifier|public
specifier|synchronized
name|MetricsContext
name|getContext
parameter_list|(
name|String
name|contextName
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
block|{
return|return
name|getContext
argument_list|(
name|contextName
argument_list|,
name|contextName
argument_list|)
return|;
block|}
comment|/**     * Returns all MetricsContexts built by this factory.    */
DECL|method|getAllContexts ()
specifier|public
specifier|synchronized
name|Collection
argument_list|<
name|MetricsContext
argument_list|>
name|getAllContexts
parameter_list|()
block|{
comment|// Make a copy to avoid race conditions with creating new contexts.
return|return
operator|new
name|ArrayList
argument_list|<
name|MetricsContext
argument_list|>
argument_list|(
name|contextMap
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a "null" context - one which does nothing.    */
DECL|method|getNullContext (String contextName)
specifier|public
specifier|static
specifier|synchronized
name|MetricsContext
name|getNullContext
parameter_list|(
name|String
name|contextName
parameter_list|)
block|{
name|MetricsContext
name|nullContext
init|=
name|nullContextMap
operator|.
name|get
argument_list|(
name|contextName
argument_list|)
decl_stmt|;
if|if
condition|(
name|nullContext
operator|==
literal|null
condition|)
block|{
name|nullContext
operator|=
operator|new
name|NullContext
argument_list|()
expr_stmt|;
name|nullContextMap
operator|.
name|put
argument_list|(
name|contextName
argument_list|,
name|nullContext
argument_list|)
expr_stmt|;
block|}
return|return
name|nullContext
return|;
block|}
comment|/**    * Returns the singleton ContextFactory instance, constructing it if     * necessary.<p/>    *     * When the instance is constructed, this method checks if the file     *<code>hadoop-metrics.properties</code> exists on the class path.  If it     * exists, it must be in the format defined by java.util.Properties, and all     * the properties in the file are set as attributes on the newly created    * ContextFactory instance.    *    * @return the singleton ContextFactory instance    */
DECL|method|getFactory ()
specifier|public
specifier|static
specifier|synchronized
name|ContextFactory
name|getFactory
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|theFactory
operator|==
literal|null
condition|)
block|{
name|theFactory
operator|=
operator|new
name|ContextFactory
argument_list|()
expr_stmt|;
name|theFactory
operator|.
name|setAttributes
argument_list|()
expr_stmt|;
block|}
return|return
name|theFactory
return|;
block|}
DECL|method|setAttributes ()
specifier|private
name|void
name|setAttributes
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|PROPERTIES_FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|//for (Object propertyNameObj : properties.keySet()) {
name|Iterator
name|it
init|=
name|properties
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|propertyName
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|propertyValue
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|setAttribute
argument_list|(
name|propertyName
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

