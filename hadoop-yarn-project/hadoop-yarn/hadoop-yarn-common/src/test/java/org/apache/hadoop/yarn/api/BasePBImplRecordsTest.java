begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
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
name|collect
operator|.
name|Lists
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
name|Maps
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
name|Sets
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraint
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
name|resource
operator|.
name|PlacementConstraints
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

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
import|import static
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
name|resource
operator|.
name|PlacementConstraints
operator|.
name|NODE
import|;
end_import

begin_import
import|import static
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
name|resource
operator|.
name|PlacementConstraints
operator|.
name|PlacementTargets
operator|.
name|allocationTag
import|;
end_import

begin_import
import|import static
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
name|resource
operator|.
name|PlacementConstraints
operator|.
name|targetIn
import|;
end_import

begin_comment
comment|/**  * Generic helper class to validate protocol records.  */
end_comment

begin_class
DECL|class|BasePBImplRecordsTest
specifier|public
class|class
name|BasePBImplRecordsTest
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BasePBImplRecordsTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:visibilitymodifier"
argument_list|)
DECL|field|typeValueCache
specifier|protected
specifier|static
name|HashMap
argument_list|<
name|Type
argument_list|,
name|Object
argument_list|>
name|typeValueCache
init|=
operator|new
name|HashMap
argument_list|<
name|Type
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:visibilitymodifier"
argument_list|)
DECL|field|excludedPropertiesMap
specifier|protected
specifier|static
name|HashMap
argument_list|<
name|Type
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|excludedPropertiesMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|rand
specifier|private
specifier|static
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|static
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[]
block|{
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|}
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|genTypeValue (Type type)
specifier|private
specifier|static
name|Object
name|genTypeValue
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|Object
name|ret
init|=
name|typeValueCache
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
condition|)
block|{
return|return
name|ret
return|;
block|}
comment|// only use positive primitive values
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|rand
operator|.
name|nextBoolean
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|byte
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|bytes
index|[
name|rand
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
index|]
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|int
operator|.
name|class
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|rand
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|long
operator|.
name|class
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
name|Long
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|float
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|rand
operator|.
name|nextFloat
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|double
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|rand
operator|.
name|nextDouble
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|String
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%c%c%c"
argument_list|,
literal|'a'
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
argument_list|,
literal|'a'
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
argument_list|,
literal|'a'
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|26
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|instanceof
name|Class
condition|)
block|{
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|type
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|Class
name|compClass
init|=
name|clazz
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|compClass
operator|!=
literal|null
condition|)
block|{
name|ret
operator|=
name|Array
operator|.
name|newInstance
argument_list|(
name|compClass
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Array
operator|.
name|set
argument_list|(
name|ret
argument_list|,
literal|0
argument_list|,
name|genTypeValue
argument_list|(
name|compClass
argument_list|)
argument_list|)
expr_stmt|;
name|Array
operator|.
name|set
argument_list|(
name|ret
argument_list|,
literal|1
argument_list|,
name|genTypeValue
argument_list|(
name|compClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|clazz
operator|.
name|isEnum
argument_list|()
condition|)
block|{
name|Object
index|[]
name|values
init|=
name|clazz
operator|.
name|getEnumConstants
argument_list|()
decl_stmt|;
name|ret
operator|=
name|values
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|values
operator|.
name|length
argument_list|)
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|.
name|equals
argument_list|(
name|ByteBuffer
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// return new ByteBuffer every time
comment|// to prevent potential side effects
name|ByteBuffer
name|buff
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|buff
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buff
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|PlacementConstraint
operator|.
name|class
argument_list|)
condition|)
block|{
name|PlacementConstraint
operator|.
name|AbstractConstraint
name|sConstraintExpr
init|=
name|targetIn
argument_list|(
name|NODE
argument_list|,
name|allocationTag
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|ret
operator|=
name|PlacementConstraints
operator|.
name|build
argument_list|(
name|sConstraintExpr
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|instanceof
name|ParameterizedType
condition|)
block|{
name|ParameterizedType
name|pt
init|=
operator|(
name|ParameterizedType
operator|)
name|type
decl_stmt|;
name|Type
name|rawType
init|=
name|pt
operator|.
name|getRawType
argument_list|()
decl_stmt|;
name|Type
index|[]
name|params
init|=
name|pt
operator|.
name|getActualTypeArguments
argument_list|()
decl_stmt|;
comment|// only support EnumSet<T>, List<T>, Set<T>, Map<K,V>
if|if
condition|(
name|rawType
operator|.
name|equals
argument_list|(
name|EnumSet
operator|.
name|class
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
index|[
literal|0
index|]
operator|instanceof
name|Class
condition|)
block|{
name|Class
name|c
init|=
call|(
name|Class
call|)
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
return|return
name|EnumSet
operator|.
name|allOf
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|rawType
operator|.
name|equals
argument_list|(
name|List
operator|.
name|class
argument_list|)
condition|)
block|{
name|ret
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|genTypeValue
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rawType
operator|.
name|equals
argument_list|(
name|Set
operator|.
name|class
argument_list|)
condition|)
block|{
name|ret
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|genTypeValue
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rawType
operator|.
name|equals
argument_list|(
name|Map
operator|.
name|class
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|genTypeValue
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|genTypeValue
argument_list|(
name|params
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|=
name|map
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type "
operator|+
name|type
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
name|typeValueCache
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * this method generate record instance by calling newIntance    * using reflection, add register the generated value to typeValueCache    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|generateByNewInstance (Class clazz)
specifier|protected
specifier|static
name|Object
name|generateByNewInstance
parameter_list|(
name|Class
name|clazz
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|ret
init|=
name|typeValueCache
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
condition|)
block|{
return|return
name|ret
return|;
block|}
name|Method
name|newInstance
init|=
literal|null
decl_stmt|;
name|Type
index|[]
name|paramTypes
init|=
operator|new
name|Type
index|[
literal|0
index|]
decl_stmt|;
comment|// get newInstance method with most parameters
for|for
control|(
name|Method
name|m
range|:
name|clazz
operator|.
name|getMethods
argument_list|()
control|)
block|{
name|int
name|mod
init|=
name|m
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|equals
argument_list|(
name|clazz
argument_list|)
operator|&&
name|Modifier
operator|.
name|isPublic
argument_list|(
name|mod
argument_list|)
operator|&&
name|Modifier
operator|.
name|isStatic
argument_list|(
name|mod
argument_list|)
operator|&&
name|m
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"newInstance"
argument_list|)
condition|)
block|{
name|Type
index|[]
name|pts
init|=
name|m
operator|.
name|getGenericParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|newInstance
operator|==
literal|null
operator|||
operator|(
name|pts
operator|.
name|length
operator|>
name|paramTypes
operator|.
name|length
operator|)
condition|)
block|{
name|newInstance
operator|=
name|m
expr_stmt|;
name|paramTypes
operator|=
name|pts
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|newInstance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" does not have newInstance method"
argument_list|)
throw|;
block|}
name|Object
index|[]
name|args
init|=
operator|new
name|Object
index|[
name|paramTypes
operator|.
name|length
index|]
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
name|args
index|[
name|i
index|]
operator|=
name|genTypeValue
argument_list|(
name|paramTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|ret
operator|=
name|newInstance
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|typeValueCache
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|class|GetSetPair
specifier|private
class|class
name|GetSetPair
block|{
DECL|field|propertyName
specifier|public
name|String
name|propertyName
decl_stmt|;
DECL|field|getMethod
specifier|public
name|Method
name|getMethod
decl_stmt|;
DECL|field|setMethod
specifier|public
name|Method
name|setMethod
decl_stmt|;
DECL|field|type
specifier|public
name|Type
name|type
decl_stmt|;
DECL|field|testValue
specifier|public
name|Object
name|testValue
decl_stmt|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"{ name=%s, class=%s, value=%s }"
argument_list|,
name|propertyName
argument_list|,
name|type
argument_list|,
name|testValue
argument_list|)
return|;
block|}
block|}
DECL|method|getGetSetPairs (Class<R> recordClass)
specifier|private
parameter_list|<
name|R
parameter_list|>
name|Map
argument_list|<
name|String
argument_list|,
name|GetSetPair
argument_list|>
name|getGetSetPairs
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|recordClass
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|GetSetPair
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|GetSetPair
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|excluded
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|excludedPropertiesMap
operator|.
name|containsKey
argument_list|(
name|recordClass
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|excluded
operator|=
name|excludedPropertiesMap
operator|.
name|get
argument_list|(
name|recordClass
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Method
index|[]
name|methods
init|=
name|recordClass
operator|.
name|getDeclaredMethods
argument_list|()
decl_stmt|;
comment|// get all get methods
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Method
name|m
init|=
name|methods
index|[
name|i
index|]
decl_stmt|;
name|int
name|mod
init|=
name|m
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|equals
argument_list|(
name|recordClass
argument_list|)
operator|&&
name|Modifier
operator|.
name|isPublic
argument_list|(
name|mod
argument_list|)
operator|&&
operator|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|mod
argument_list|)
operator|)
condition|)
block|{
name|String
name|name
init|=
name|m
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"getProto"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|(
name|name
operator|.
name|length
argument_list|()
operator|>
literal|3
operator|)
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
operator|&&
operator|(
name|m
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|)
condition|)
block|{
name|String
name|propertyName
init|=
name|name
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|Type
name|valueType
init|=
name|m
operator|.
name|getGenericReturnType
argument_list|()
decl_stmt|;
name|GetSetPair
name|p
init|=
name|ret
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
operator|new
name|GetSetPair
argument_list|()
expr_stmt|;
name|p
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|p
operator|.
name|type
operator|=
name|valueType
expr_stmt|;
name|p
operator|.
name|getMethod
operator|=
name|m
expr_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Multiple get method with same name: "
operator|+
name|recordClass
operator|+
name|p
operator|.
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// match get methods with set methods
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Method
name|m
init|=
name|methods
index|[
name|i
index|]
decl_stmt|;
name|int
name|mod
init|=
name|m
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|equals
argument_list|(
name|recordClass
argument_list|)
operator|&&
name|Modifier
operator|.
name|isPublic
argument_list|(
name|mod
argument_list|)
operator|&&
operator|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|mod
argument_list|)
operator|)
condition|)
block|{
name|String
name|name
init|=
name|m
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
operator|&&
operator|(
name|m
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|)
condition|)
block|{
name|String
name|propertyName
init|=
name|name
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|Type
name|valueType
init|=
name|m
operator|.
name|getGenericParameterTypes
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|GetSetPair
name|p
init|=
name|ret
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|p
operator|.
name|type
operator|.
name|equals
argument_list|(
name|valueType
argument_list|)
condition|)
block|{
name|p
operator|.
name|setMethod
operator|=
name|m
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// exclude incomplete get/set pair, and generate test value
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|GetSetPair
argument_list|>
argument_list|>
name|itr
init|=
name|ret
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|GetSetPair
argument_list|>
name|cur
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|GetSetPair
name|gsp
init|=
name|cur
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|gsp
operator|.
name|getMethod
operator|==
literal|null
operator|)
operator|||
operator|(
name|gsp
operator|.
name|setMethod
operator|==
literal|null
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Exclude potential property: %s\n"
argument_list|,
name|gsp
operator|.
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
name|itr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|excluded
operator|!=
literal|null
operator|&&
name|excluded
operator|.
name|contains
argument_list|(
name|gsp
operator|.
name|propertyName
argument_list|)
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Excluding potential property(present in exclusion list): %s\n"
argument_list|,
name|gsp
operator|.
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
name|itr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"New property: %s type: %s"
argument_list|,
name|gsp
operator|.
name|toString
argument_list|()
argument_list|,
name|gsp
operator|.
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|gsp
operator|.
name|testValue
operator|=
name|genTypeValue
argument_list|(
name|gsp
operator|.
name|type
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|" testValue: %s\n"
argument_list|,
name|gsp
operator|.
name|testValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|validatePBImplRecord (Class<R> recordClass, Class<P> protoClass)
specifier|protected
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|void
name|validatePBImplRecord
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|recordClass
parameter_list|,
name|Class
argument_list|<
name|P
argument_list|>
name|protoClass
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Validate %s %s\n"
argument_list|,
name|recordClass
operator|.
name|getName
argument_list|()
argument_list|,
name|protoClass
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Constructor
argument_list|<
name|R
argument_list|>
name|emptyConstructor
init|=
name|recordClass
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
name|Constructor
argument_list|<
name|R
argument_list|>
name|pbConstructor
init|=
name|recordClass
operator|.
name|getConstructor
argument_list|(
name|protoClass
argument_list|)
decl_stmt|;
name|Method
name|getProto
init|=
name|recordClass
operator|.
name|getDeclaredMethod
argument_list|(
literal|"getProto"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|GetSetPair
argument_list|>
name|getSetPairs
init|=
name|getGetSetPairs
argument_list|(
name|recordClass
argument_list|)
decl_stmt|;
name|R
name|origRecord
init|=
name|emptyConstructor
operator|.
name|newInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|GetSetPair
name|gsp
range|:
name|getSetPairs
operator|.
name|values
argument_list|()
control|)
block|{
name|gsp
operator|.
name|setMethod
operator|.
name|invoke
argument_list|(
name|origRecord
argument_list|,
name|gsp
operator|.
name|testValue
argument_list|)
expr_stmt|;
block|}
name|Object
name|ret
init|=
name|getProto
operator|.
name|invoke
argument_list|(
name|origRecord
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|recordClass
operator|.
name|getName
argument_list|()
operator|+
literal|"#getProto returns null"
argument_list|,
name|ret
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|protoClass
operator|.
name|isAssignableFrom
argument_list|(
name|ret
operator|.
name|getClass
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Illegal getProto method return type: "
operator|+
name|ret
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|R
name|deserRecord
init|=
name|pbConstructor
operator|.
name|newInstance
argument_list|(
name|ret
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"whole "
operator|+
name|recordClass
operator|+
literal|" records should be equal"
argument_list|,
name|origRecord
argument_list|,
name|deserRecord
argument_list|)
expr_stmt|;
for|for
control|(
name|GetSetPair
name|gsp
range|:
name|getSetPairs
operator|.
name|values
argument_list|()
control|)
block|{
name|Object
name|origValue
init|=
name|gsp
operator|.
name|getMethod
operator|.
name|invoke
argument_list|(
name|origRecord
argument_list|)
decl_stmt|;
name|Object
name|deserValue
init|=
name|gsp
operator|.
name|getMethod
operator|.
name|invoke
argument_list|(
name|deserRecord
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"property "
operator|+
name|recordClass
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|gsp
operator|.
name|propertyName
operator|+
literal|" should be equal"
argument_list|,
name|origValue
argument_list|,
name|deserValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

