begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
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
name|resourcemanager
operator|.
name|applicationsmanager
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|protocolrecords
operator|.
name|SubmitApplicationRequest
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
name|ApplicationId
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
name|ApplicationReport
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
name|YarnApplicationState
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
name|ApplicationSubmissionContext
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
name|Container
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
name|Priority
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
name|QueueInfo
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
name|QueueUserACLInfo
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
name|Resource
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
name|ResourceRequest
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
name|conf
operator|.
name|YarnConfiguration
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
name|event
operator|.
name|EventHandler
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|resourcemanager
operator|.
name|ClientRMService
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
name|resourcemanager
operator|.
name|RMContext
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
name|resourcemanager
operator|.
name|RMContextImpl
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
name|resourcemanager
operator|.
name|ResourceManager
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
name|resourcemanager
operator|.
name|amlauncher
operator|.
name|AMLauncherEventType
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|Allocation
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|YarnScheduler
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
name|resourcemanager
operator|.
name|security
operator|.
name|AMRMTokenSecretManager
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/* a test case that tests the launch failure of a AM */
end_comment

begin_class
DECL|class|TestAMLaunchFailure
specifier|public
class|class
name|TestAMLaunchFailure
block|{
comment|//  private static final Log LOG = LogFactory.getLog(TestAMLaunchFailure.class);
comment|//  private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
comment|//  ApplicationsManagerImpl asmImpl;
comment|//  YarnScheduler scheduler = new DummyYarnScheduler();
comment|//  ApplicationTokenSecretManager applicationTokenSecretManager =
comment|//    new ApplicationTokenSecretManager();
comment|//  private ClientRMService clientService;
comment|//
comment|//  private RMContext context;
comment|//
comment|//  private static class DummyYarnScheduler implements YarnScheduler {
comment|//    private Container container = recordFactory.newRecordInstance(Container.class);
comment|//
comment|//    @Override
comment|//    public Allocation allocate(ApplicationId applicationId,
comment|//        List<ResourceRequest> ask, List<Container> release) throws IOException {
comment|//      return new Allocation(Arrays.asList(container), Resources.none());
comment|//    }
comment|//
comment|//    @Override
comment|//    public QueueInfo getQueueInfo(String queueName,
comment|//        boolean includeChildQueues,
comment|//        boolean recursive) throws IOException {
comment|//      return null;
comment|//    }
comment|//
comment|//    @Override
comment|//    public List<QueueUserACLInfo> getQueueUserAclInfo() {
comment|//      return null;
comment|//    }
comment|//
comment|//    @Override
comment|//    public void addApplication(ApplicationId applicationId,
comment|//        ApplicationMaster master, String user, String queue, Priority priority
comment|//        , ApplicationStore appStore)
comment|//        throws IOException {
comment|//      // TODO Auto-generated method stub
comment|//
comment|//    }
comment|//
comment|//    @Override
comment|//    public Resource getMaximumResourceCapability() {
comment|//      // TODO Auto-generated method stub
comment|//      return null;
comment|//    }
comment|//
comment|//    @Override
comment|//    public Resource getMinimumResourceCapability() {
comment|//      // TODO Auto-generated method stub
comment|//      return null;
comment|//    }
comment|//  }
comment|//
comment|//  private class DummyApplicationTracker implements EventHandler<ASMEvent<ApplicationTrackerEventType>> {
comment|//    public DummyApplicationTracker() {
comment|//      context.getDispatcher().register(ApplicationTrackerEventType.class, this);
comment|//    }
comment|//    @Override
comment|//    public void handle(ASMEvent<ApplicationTrackerEventType> event) {
comment|//    }
comment|//  }
comment|//
comment|//  public class ExtApplicationsManagerImpl extends ApplicationsManagerImpl {
comment|//
comment|//    private  class DummyApplicationMasterLauncher implements EventHandler<ASMEvent<AMLauncherEventType>> {
comment|//      private AtomicInteger notify = new AtomicInteger();
comment|//      private AppAttempt app;
comment|//
comment|//      public DummyApplicationMasterLauncher(RMContext context) {
comment|//        context.getDispatcher().register(AMLauncherEventType.class, this);
comment|//        new TestThread().start();
comment|//      }
comment|//      @Override
comment|//      public void handle(ASMEvent<AMLauncherEventType> appEvent) {
comment|//        switch(appEvent.getType()) {
comment|//        case LAUNCH:
comment|//          LOG.info("LAUNCH called ");
comment|//          app = appEvent.getApplication();
comment|//          synchronized (notify) {
comment|//            notify.addAndGet(1);
comment|//            notify.notify();
comment|//          }
comment|//          break;
comment|//        }
comment|//      }
comment|//
comment|//      private class TestThread extends Thread {
comment|//        public void run() {
comment|//          synchronized(notify) {
comment|//            try {
comment|//              while (notify.get() == 0) {
comment|//                notify.wait();
comment|//              }
comment|//            } catch (InterruptedException e) {
comment|//              e.printStackTrace();
comment|//            }
comment|//            context.getDispatcher().getEventHandler().handle(
comment|//                new ApplicationEvent(ApplicationEventType.LAUNCHED,
comment|//                    app.getApplicationID()));
comment|//          }
comment|//        }
comment|//      }
comment|//    }
comment|//
comment|//    public ExtApplicationsManagerImpl(
comment|//        ApplicationTokenSecretManager applicationTokenSecretManager,
comment|//        YarnScheduler scheduler) {
comment|//      super(applicationTokenSecretManager, scheduler, context);
comment|//    }
comment|//
comment|//    @Override
comment|//    protected EventHandler<ASMEvent<AMLauncherEventType>> createNewApplicationMasterLauncher(
comment|//        ApplicationTokenSecretManager tokenSecretManager) {
comment|//      return new DummyApplicationMasterLauncher(context);
comment|//    }
comment|//  }
comment|//
comment|//
comment|//  @Before
comment|//  public void setUp() {
comment|//    context = new RMContextImpl(new MemStore());
comment|//    Configuration conf = new Configuration();
comment|//
comment|//    context.getDispatcher().register(ApplicationEventType.class,
comment|//        new ResourceManager.ApplicationEventDispatcher(context));
comment|//
comment|//    context.getDispatcher().init(conf);
comment|//    context.getDispatcher().start();
comment|//
comment|//    asmImpl = new ExtApplicationsManagerImpl(applicationTokenSecretManager, scheduler);
comment|//    clientService = new ClientRMService(context, asmImpl
comment|//        .getAmLivelinessMonitor(), asmImpl.getClientToAMSecretManager(),
comment|//        scheduler);
comment|//    clientService.init(conf);
comment|//    new DummyApplicationTracker();
comment|//    conf.setLong(YarnConfiguration.AM_EXPIRY_INTERVAL, 3000L);
comment|//    conf.setInt(RMConfig.AM_MAX_RETRIES, 1);
comment|//    asmImpl.init(conf);
comment|//    asmImpl.start();
comment|//  }
comment|//
comment|//  @After
comment|//  public void tearDown() {
comment|//    asmImpl.stop();
comment|//  }
comment|//
comment|//  private ApplicationSubmissionContext createDummyAppContext(ApplicationId appID) {
comment|//    ApplicationSubmissionContext context = recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
comment|//    context.setApplicationId(appID);
comment|//    return context;
comment|//  }
comment|//
comment|//  @Test
comment|//  public void testAMLaunchFailure() throws Exception {
comment|//    ApplicationId appID = clientService.getNewApplicationId();
comment|//    ApplicationSubmissionContext submissionContext = createDummyAppContext(appID);
comment|//    SubmitApplicationRequest request = recordFactory
comment|//        .newRecordInstance(SubmitApplicationRequest.class);
comment|//    request.setApplicationSubmissionContext(submissionContext);
comment|//    clientService.submitApplication(request);
comment|//    AppAttempt application = context.getApplications().get(appID);
comment|//
comment|//    while (application.getState() != ApplicationState.FAILED) {
comment|//      LOG.info("Waiting for application to go to FAILED state."
comment|//          + " Current state is " + application.getState());
comment|//      Thread.sleep(200);
comment|//      application = context.getApplications().get(appID);
comment|//    }
comment|//    Assert.assertEquals(ApplicationState.FAILED, application.getState());
comment|//  }
block|}
end_class

end_unit

