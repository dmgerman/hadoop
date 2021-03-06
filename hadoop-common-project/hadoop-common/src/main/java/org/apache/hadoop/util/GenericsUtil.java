begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|classification
operator|.
name|InterfaceStability
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
comment|/**  * Contains utility methods for dealing with Java Generics.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|GenericsUtil
specifier|public
class|class
name|GenericsUtil
block|{
comment|/**    * Returns the Class object (of type<code>Class&lt;T&gt;</code>) of the      * argument of type<code>T</code>.     * @param<T> The type of the argument    * @param t the object to get it class    * @return<code>Class&lt;T&gt;</code>    */
DECL|method|getClass (T t)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|T
argument_list|>
name|getClass
parameter_list|(
name|T
name|t
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
init|=
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|t
operator|.
name|getClass
argument_list|()
decl_stmt|;
return|return
name|clazz
return|;
block|}
comment|/**    * Converts the given<code>List&lt;T&gt;</code> to a an array of     *<code>T[]</code>.    * @param c the Class object of the items in the list    * @param list the list to convert    */
DECL|method|toArray (Class<T> c, List<T> list)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|toArray
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
index|[]
name|ta
init|=
operator|(
name|T
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|c
argument_list|,
name|list
operator|.
name|size
argument_list|()
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|ta
index|[
name|i
index|]
operator|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|ta
return|;
block|}
comment|/**    * Converts the given<code>List&lt;T&gt;</code> to a an array of     *<code>T[]</code>.     * @param list the list to convert    * @throws ArrayIndexOutOfBoundsException if the list is empty.     * Use {@link #toArray(Class, List)} if the list may be empty.    */
DECL|method|toArray (List<T> list)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|toArray
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|toArray
argument_list|(
name|getClass
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|list
argument_list|)
return|;
block|}
comment|/**    * Determine whether the log of<code>clazz</code> is Log4j implementation.    * @param clazz a class to be determined    * @return true if the log of<code>clazz</code> is Log4j implementation.    */
DECL|method|isLog4jLogger (Class<?> clazz)
specifier|public
specifier|static
name|boolean
name|isLog4jLogger
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
name|log4jClass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.slf4j.impl.Log4jLoggerAdapter"
argument_list|)
decl_stmt|;
return|return
name|log4jClass
operator|.
name|isInstance
argument_list|(
name|log
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

