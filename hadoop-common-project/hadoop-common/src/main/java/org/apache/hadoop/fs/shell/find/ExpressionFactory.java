begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell.find
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|find
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|util
operator|.
name|ReflectionUtils
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Factory class for registering and searching for expressions for use in the  * {@link org.apache.hadoop.fs.shell.find.Find} command.  */
end_comment

begin_class
DECL|class|ExpressionFactory
specifier|final
class|class
name|ExpressionFactory
block|{
DECL|field|REGISTER_EXPRESSION_METHOD
specifier|private
specifier|static
specifier|final
name|String
name|REGISTER_EXPRESSION_METHOD
init|=
literal|"registerExpression"
decl_stmt|;
DECL|field|expressionMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
argument_list|>
name|expressionMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|ExpressionFactory
name|INSTANCE
init|=
operator|new
name|ExpressionFactory
argument_list|()
decl_stmt|;
DECL|method|getExpressionFactory ()
specifier|static
name|ExpressionFactory
name|getExpressionFactory
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
comment|/**    * Private constructor to ensure singleton.    */
DECL|method|ExpressionFactory ()
specifier|private
name|ExpressionFactory
parameter_list|()
block|{   }
comment|/**    * Invokes "static void registerExpression(FindExpressionFactory)" on the    * given class. This method abstracts the contract between the factory and the    * expression class. Do not assume that directly invoking registerExpression    * on the given class will have the same effect.    *    * @param expressionClass    *          class to allow an opportunity to register    */
DECL|method|registerExpression (Class<? extends Expression> expressionClass)
name|void
name|registerExpression
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|expressionClass
parameter_list|)
block|{
try|try
block|{
name|Method
name|register
init|=
name|expressionClass
operator|.
name|getMethod
argument_list|(
name|REGISTER_EXPRESSION_METHOD
argument_list|,
name|ExpressionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|register
operator|!=
literal|null
condition|)
block|{
name|register
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|this
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
name|RuntimeException
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Register the given class as handling the given list of expression names.    *    * @param expressionClass    *          the class implementing the expression names    * @param names    *          one or more command names that will invoke this class    * @throws IOException    *           if the expression is not of an expected type    */
DECL|method|addClass (Class<? extends Expression> expressionClass, String... names)
name|void
name|addClass
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|expressionClass
parameter_list|,
name|String
modifier|...
name|names
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
name|expressionMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|expressionClass
argument_list|)
expr_stmt|;
block|}
comment|/**    * Determines whether the given expression name represents and actual    * expression.    *    * @param expressionName    *          name of the expression    * @return true if expressionName represents an expression    */
DECL|method|isExpression (String expressionName)
name|boolean
name|isExpression
parameter_list|(
name|String
name|expressionName
parameter_list|)
block|{
return|return
name|expressionMap
operator|.
name|containsKey
argument_list|(
name|expressionName
argument_list|)
return|;
block|}
comment|/**    * Get an instance of the requested expression    *    * @param expressionName    *          name of the command to lookup    * @param conf    *          the Hadoop configuration    * @return the {@link Expression} or null if the expression is unknown    */
DECL|method|getExpression (String expressionName, Configuration conf)
name|Expression
name|getExpression
parameter_list|(
name|String
name|expressionName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"configuration is null"
argument_list|)
throw|;
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|expressionClass
init|=
name|expressionMap
operator|.
name|get
argument_list|(
name|expressionName
argument_list|)
decl_stmt|;
name|Expression
name|instance
init|=
name|createExpression
argument_list|(
name|expressionClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|instance
return|;
block|}
comment|/**    * Creates an instance of the requested {@link Expression} class.    *    * @param expressionClass    *          {@link Expression} class to be instantiated    * @param conf    *          the Hadoop configuration    * @return a new instance of the requested {@link Expression} class    */
DECL|method|createExpression ( Class<? extends Expression> expressionClass, Configuration conf)
name|Expression
name|createExpression
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|expressionClass
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Expression
name|instance
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|expressionClass
operator|!=
literal|null
condition|)
block|{
name|instance
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|expressionClass
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
comment|/**    * Creates an instance of the requested {@link Expression} class.    *    * @param expressionClassname    *          name of the {@link Expression} class to be instantiated    * @param conf    *          the Hadoop configuration    * @return a new instance of the requested {@link Expression} class    */
DECL|method|createExpression (String expressionClassname, Configuration conf)
name|Expression
name|createExpression
parameter_list|(
name|String
name|expressionClassname
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Expression
argument_list|>
name|expressionClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|expressionClassname
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Expression
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|createExpression
argument_list|(
name|expressionClass
argument_list|,
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid classname "
operator|+
name|expressionClassname
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

