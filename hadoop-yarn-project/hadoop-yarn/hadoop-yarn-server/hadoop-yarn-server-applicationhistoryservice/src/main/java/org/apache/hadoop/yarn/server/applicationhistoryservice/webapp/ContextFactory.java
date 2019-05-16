begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|applicationhistoryservice
operator|.
name|webapp
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineEntity
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
name|records
operator|.
name|timeline
operator|.
name|TimelineEntities
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
name|records
operator|.
name|timeline
operator|.
name|TimelineDomain
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
name|records
operator|.
name|timeline
operator|.
name|TimelineDomains
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
name|records
operator|.
name|timeline
operator|.
name|TimelineEvents
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
name|records
operator|.
name|timeline
operator|.
name|TimelinePutResponse
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
name|server
operator|.
name|webapp
operator|.
name|dao
operator|.
name|AppInfo
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
name|server
operator|.
name|webapp
operator|.
name|dao
operator|.
name|AppsInfo
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
name|server
operator|.
name|webapp
operator|.
name|dao
operator|.
name|AppAttemptInfo
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
name|server
operator|.
name|webapp
operator|.
name|dao
operator|.
name|AppAttemptsInfo
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
name|server
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ContainerInfo
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
name|server
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ContainersInfo
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
name|webapp
operator|.
name|RemoteExceptionData
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
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBException
import|;
end_import

begin_comment
comment|/**  * ContextFactory to reuse JAXBContextImpl for DAO Classes.  */
end_comment

begin_class
DECL|class|ContextFactory
specifier|public
specifier|final
class|class
name|ContextFactory
block|{
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
name|ContextFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cacheContext
specifier|private
specifier|static
name|JAXBContext
name|cacheContext
decl_stmt|;
comment|// All the dao classes from TimelineWebService and AHSWebService
comment|// added except TimelineEntity and TimelineEntities
DECL|field|CTYPES
specifier|private
specifier|static
specifier|final
name|Class
index|[]
name|CTYPES
init|=
block|{
name|AppInfo
operator|.
name|class
block|,
name|AppsInfo
operator|.
name|class
block|,
name|AppAttemptInfo
operator|.
name|class
block|,
name|AppAttemptsInfo
operator|.
name|class
block|,
name|ContainerInfo
operator|.
name|class
block|,
name|ContainersInfo
operator|.
name|class
block|,
name|RemoteExceptionData
operator|.
name|class
block|,
name|TimelineDomain
operator|.
name|class
block|,
name|TimelineDomains
operator|.
name|class
block|,
name|TimelineEvents
operator|.
name|class
block|,
name|TimelinePutResponse
operator|.
name|class
block|}
decl_stmt|;
DECL|field|CLASS_SET
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|>
name|CLASS_SET
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|CTYPES
argument_list|)
argument_list|)
decl_stmt|;
comment|// TimelineEntity has java.util.Set interface which JAXB
comment|// can't handle and throws IllegalAnnotationExceptions
DECL|field|IGNORE_TYPES
specifier|private
specifier|static
specifier|final
name|Class
index|[]
name|IGNORE_TYPES
init|=
block|{
name|TimelineEntity
operator|.
name|class
block|,
name|TimelineEntities
operator|.
name|class
block|}
decl_stmt|;
DECL|field|IGNORE_SET
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|>
name|IGNORE_SET
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|IGNORE_TYPES
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|je
specifier|private
specifier|static
name|JAXBException
name|je
init|=
operator|new
name|JAXBException
argument_list|(
literal|"TimelineEntity and TimelineEntities has "
operator|+
literal|"IllegalAnnotation"
argument_list|)
decl_stmt|;
DECL|field|stackTrace
specifier|private
specifier|static
name|StackTraceElement
index|[]
name|stackTrace
init|=
operator|new
name|StackTraceElement
index|[]
block|{
operator|new
name|StackTraceElement
argument_list|(
name|ContextFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"createContext"
argument_list|,
literal|"ContextFactory.java"
argument_list|,
operator|-
literal|1
argument_list|)
block|}
decl_stmt|;
DECL|method|ContextFactory ()
specifier|private
name|ContextFactory
parameter_list|()
block|{   }
DECL|method|newContext (Class[] classes, Map<String, Object> properties)
specifier|public
specifier|static
name|JAXBContext
name|newContext
parameter_list|(
name|Class
index|[]
name|classes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
name|spFactory
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.sun.xml.internal.bind.v2.ContextFactory"
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|spFactory
operator|.
name|getMethod
argument_list|(
literal|"createContext"
argument_list|,
name|Class
index|[]
operator|.
expr|class
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|(
name|JAXBContext
operator|)
name|m
operator|.
name|invoke
argument_list|(
operator|(
name|Object
operator|)
literal|null
argument_list|,
name|classes
argument_list|,
name|properties
argument_list|)
return|;
block|}
comment|// Called from WebComponent.service
DECL|method|createContext (Class[] classes, Map<String, Object> properties)
specifier|public
specifier|static
name|JAXBContext
name|createContext
parameter_list|(
name|Class
index|[]
name|classes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Class
name|c
range|:
name|classes
control|)
block|{
if|if
condition|(
name|IGNORE_SET
operator|.
name|contains
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|je
operator|.
name|setStackTrace
argument_list|(
name|stackTrace
argument_list|)
expr_stmt|;
throw|throw
name|je
throw|;
block|}
if|if
condition|(
operator|!
name|CLASS_SET
operator|.
name|contains
argument_list|(
name|c
argument_list|)
condition|)
block|{
try|try
block|{
return|return
name|newContext
argument_list|(
name|classes
argument_list|,
name|properties
argument_list|)
return|;
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
literal|"Error while Creating JAXBContext"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
try|try
block|{
synchronized|synchronized
init|(
name|ContextFactory
operator|.
name|class
init|)
block|{
if|if
condition|(
name|cacheContext
operator|==
literal|null
condition|)
block|{
name|cacheContext
operator|=
name|newContext
argument_list|(
name|CTYPES
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Error while Creating JAXBContext"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
return|return
name|cacheContext
return|;
block|}
comment|// Called from WebComponent.init
DECL|method|createContext (String contextPath, ClassLoader classLoader, Map<String, Object> properties)
specifier|public
specifier|static
name|JAXBContext
name|createContext
parameter_list|(
name|String
name|contextPath
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
name|spFactory
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.sun.xml.internal.bind.v2.ContextFactory"
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|spFactory
operator|.
name|getMethod
argument_list|(
literal|"createContext"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|ClassLoader
operator|.
name|class
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|(
name|JAXBContext
operator|)
name|m
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|contextPath
argument_list|,
name|classLoader
argument_list|,
name|properties
argument_list|)
return|;
block|}
block|}
end_class

end_unit

